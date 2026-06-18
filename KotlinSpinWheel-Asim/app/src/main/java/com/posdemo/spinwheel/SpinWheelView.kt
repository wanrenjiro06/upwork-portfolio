package com.posdemo.spinwheel

import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * SpinWheelView
 * ----------------------------------------------------------------------------
 * A self-contained "Spin & Win" prize wheel for a customer-facing POS display.
 *
 * Design goals (mirrors the web demo at https://spin-and-win-demo.netlify.app):
 *   • The wheel FACE is pre-rendered once to a Bitmap on size change, then the
 *     render loop only rotates/composites it — keeps every frame cheap (60fps).
 *   • Realistic deceleration via easeOutQuart over ~5s + 6–8 full turns.
 *   • The pointer physically flicks/ticks against each peg as it passes, with a
 *     spring (Hooke's law) settling it back — the detail that sells the feel.
 *   • Confetti burst + result callback on landing.
 *
 * PRODUCTION HOOK — this is the important part for a real POS:
 *   The business/back-office decides the prize (weighted odds, stock limits,
 *   per-customer rules). Call [spinTo] with that predetermined index and the
 *   wheel simply animates to land there. Use [spin] (random) only for demos.
 *
 * Usage:
 *   wheel.onResult = { index, label -> /* record redemption, print coupon */ }
 *   wheel.spinTo(serverChosenIndex)      // production
 *   wheel.spin()                         // demo / random
 */
class SpinWheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    // ---- Public API ---------------------------------------------------------

    data class Segment(val label: String, val color: Int, val glow: Int)

    /** The wheel slices, clockwise from the 3-o'clock position. */
    var segments: List<Segment> = DEFAULT_SEGMENTS
        set(value) {
            field = value
            faceDirty = true
            invalidate()
        }

    /** Fired on the UI thread when the wheel comes to rest. */
    var onResult: ((index: Int, label: String) -> Unit)? = null

    /** Procedural tick/fanfare audio. Set false for silent kiosks. */
    var soundEnabled: Boolean = true

    /** Honors the device "remove animations" accessibility setting when true. */
    var respectReducedMotion: Boolean = true

    val isSpinning: Boolean get() = spinning

    // ---- Geometry -----------------------------------------------------------

    private var cx = 0f
    private var cy = 0f
    private var radius = 0f
    private val seg get() = TWO_PI / segments.size

    private var face: Bitmap? = null
    private var faceDirty = true

    // ---- Spin state ---------------------------------------------------------

    private var rotation = Random.nextFloat() * TWO_PI   // radians
    private var spinning = false
    private var spinStartNs = 0L
    private var spinDurMs = 0f
    private var startRot = 0f
    private var targetRot = 0f
    private var chosen = 0
    private var lastTickIdx = -999

    // pointer flick spring
    private var flickAngle = 0f
    private var flickVel = 0f

    // idle ambient
    private var idlePhase = 0f

    // frame timing
    private var lastFrameNs = 0L

    // confetti
    private val confetti = ArrayList<Confetti>()

    private val audio: WheelAudio by lazy { WheelAudio(context) }

    // ---- Paints (allocated once — never allocate in onDraw) -----------------

    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(18, 255, 255, 255)
    }
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(41, 0, 0, 0)
        strokeWidth = 1.4f
        style = Paint.Style.STROKE
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.RIGHT
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    private val labelShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.RIGHT
        color = Color.argb(72, 0, 0, 0)
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    private val bezelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val bulbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hubPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hubRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#D8A838")
    }
    private val hubTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFE9A8")
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.argb(128, 120, 80, 0)
        strokeWidth = 1.4f
    }
    private val knobPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFE9A8")
    }
    private val confettiPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val oval = RectF()
    private val pointerPath = android.graphics.Path()

    // ---- Render loop --------------------------------------------------------

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            step(frameTimeNanos)
            if (isAttachedToWindow) Choreographer.getInstance().postFrameCallback(this)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lastFrameNs = 0L
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    override fun onDetachedFromWindow() {
        Choreographer.getInstance().removeFrameCallback(frameCallback)
        face?.recycle()
        face = null
        audio.release()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        cx = w / 2f
        cy = h / 2f
        val d = min(w, h).toFloat()
        radius = max(d * 0.5f - d * 0.085f, 60f)
        faceDirty = true
    }

    // ---- Public spin entry points ------------------------------------------

    /** Spin to a server/business-chosen prize. The wheel lands on [index]. */
    fun spinTo(index: Int) {
        if (spinning || segments.isEmpty()) return
        startSpin(index.coerceIn(0, segments.size - 1))
    }

    /** Demo helper: spin to a uniformly random prize. */
    fun spin() {
        if (spinning || segments.isEmpty()) return
        startSpin(Random.nextInt(segments.size))
    }

    private fun startSpin(index: Int) {
        spinning = true
        chosen = index
        clearConfetti()

        // Where must the wheel stop so that `chosen` sits under the top pointer?
        var desired = POINTER - (chosen + 0.5f) * seg
        desired = mod(desired, TWO_PI)
        val cur = mod(rotation, TWO_PI)
        var delta = desired - cur
        if (delta < 0) delta += TWO_PI

        val reduced = respectReducedMotion && isReducedMotion()
        val fullSpins = if (reduced) 2 else 6 + Random.nextInt(3)   // 6–8 turns
        startRot = rotation
        targetRot = rotation + delta + fullSpins * TWO_PI
        spinDurMs = if (reduced) 1400f else 4600f + Random.nextFloat() * 900f
        spinStartNs = System.nanoTime()
        lastTickIdx = segUnderPointer(rotation)
        if (soundEnabled) audio.warm()
    }

    // ---- Per-frame update ---------------------------------------------------

    private fun step(nowNs: Long) {
        val dt = if (lastFrameNs == 0L) 1f
        else min((nowNs - lastFrameNs) / 16_666_667f, 3f)   // frames since last, clamped
        lastFrameNs = nowNs

        if (spinning) {
            val t = min((nowNs - spinStartNs) / 1_000_000f / spinDurMs, 1f)
            val e = EASE_OUT_QUART.getInterpolation(t)
            rotation = startRot + (targetRot - startRot) * e

            val idx = segUnderPointer(rotation)
            if (idx != lastTickIdx) {
                val speed = 1f - t
                if (soundEnabled) audio.tick(speed)
                flickVel -= 0.9f + speed * 1.4f       // kick the pointer
                lastTickIdx = idx
            }
            if (t >= 1f) {
                spinning = false
                rotation = mod(targetRot, TWO_PI)
                onLanded(chosen)
            }
        } else {
            idlePhase += dt * 0.05f
        }

        // pointer flick spring (critically-damped-ish settle)
        flickVel += (-22f * flickAngle - 6f * flickVel) * (dt / 60f) * 6f
        flickAngle += flickVel * (dt / 60f)

        if (confetti.isNotEmpty()) updateConfetti(dt)

        invalidate()
    }

    private fun onLanded(index: Int) {
        burstConfetti()
        if (soundEnabled) audio.fanfare()
        post { onResult?.invoke(index, segments[index].label) }
    }

    // ---- Drawing ------------------------------------------------------------

    override fun onDraw(canvas: Canvas) {
        if (radius <= 0f) return
        if (faceDirty || face == null) renderFace()

        drawBezel(canvas)

        // rotating face
        canvas.save()
        canvas.rotate(toDeg(rotation), cx, cy)
        face?.let { canvas.drawBitmap(it, cx - radius, cy - radius, null) }
        canvas.restore()

        drawHub(canvas)
        drawPointer(canvas)
        if (confetti.isNotEmpty()) drawConfetti(canvas)
    }

    /** Pre-render slices + labels + pegs once. Called only on resize/segment change. */
    private fun renderFace() {
        val size = (radius * 2).toInt().coerceAtLeast(2)
        face?.recycle()
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val r = radius
        val center = r
        oval.set(center - r, center - r, center + r, center + r)

        val labelFs = max(11f, r * 0.092f)
        labelPaint.textSize = labelFs
        labelShadowPaint.textSize = labelFs

        for (i in segments.indices) {
            val a0 = i * seg
            val s = segments[i]

            // sector with radial depth (light core -> darker rim)
            sectorPaint.shader = RadialGradient(
                center, center, r,
                intArrayOf(shade(s.color, 18), s.color, shade(s.color, -20)),
                floatArrayOf(0f, 0.62f, 1f),
                Shader.TileMode.CLAMP
            )
            c.drawArc(oval, toDeg(a0), toDeg(seg), true, sectorPaint)

            // gloss on the leading half of each slice
            c.drawArc(oval, toDeg(a0), toDeg(seg) * 0.5f, true, glossPaint)

            // divider line at slice start
            c.save()
            c.rotate(toDeg(a0), center, center)
            c.drawLine(center, center, center + r, center, dividerPaint)
            c.restore()

            // label, right-aligned, running outward
            c.save()
            c.rotate(toDeg(a0 + seg / 2f), center, center)
            val words = s.label.split(' ')
            drawLabel(c, words, center, center + r * 0.9f + 0.8f, labelFs, labelShadowPaint)
            drawLabel(c, words, center, center + r * 0.9f, labelFs, labelPaint.apply { color = Color.WHITE })
            c.restore()
        }

        // inner shadow disc where the hub sits
        sectorPaint.shader = null
        sectorPaint.color = Color.argb(51, 0, 0, 0)
        c.drawCircle(center, center, r * 0.165f, sectorPaint)

        face = bmp
        faceDirty = false
    }

    private fun drawLabel(c: Canvas, words: List<String>, x: Float, baseX: Float, fs: Float, p: Paint) {
        // baseX is the radial distance from center; rotate already applied, so draw
        // along +x with the text vertically stacked around the slice mid-line.
        val fm = p.fontMetrics
        val vCenter = -(fm.ascent + fm.descent) / 2f   // baseline offset for visual centering
        when (words.size) {
            1 -> c.drawText(words[0], baseX, x + vCenter, p)
            2 -> {
                c.drawText(words[0], baseX, x - fs * 0.58f + vCenter, p)
                c.drawText(words[1], baseX, x + fs * 0.58f + vCenter, p)
            }
            else -> {
                val start = -(words.size - 1) / 2f
                words.forEachIndexed { k, w ->
                    c.drawText(w, baseX, x + (start + k) * fs * 0.98f + vCenter, p)
                }
            }
        }
    }

    private fun drawBezel(canvas: Canvas) {
        val rOuter = radius + radius * 0.072f
        bezelPaint.shader = LinearGradient(
            cx - rOuter, cy - rOuter, cx + rOuter, cy + rOuter,
            intArrayOf(
                Color.parseColor("#FFE9A8"),
                Color.parseColor("#C9962B"),
                Color.parseColor("#8A651A")
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        bezelPaint.strokeWidth = radius * 0.085f
        canvas.drawCircle(cx, cy, rOuter, bezelPaint)

        // chasing rim bulbs
        val bulbs = segments.size * 2
        val now = System.nanoTime() / 1_000_000f
        val rad = radius * 0.026f
        for (i in 0 until bulbs) {
            val a = (i.toFloat() / bulbs) * TWO_PI - HALF_PI
            val bx = cx + cos(a) * rOuter
            val by = cy + sin(a) * rOuter
            val lit = if (spinning)
                ((now / 90f).toInt() + i) % 2 == 0
            else
                sin(idlePhase * 3f - i * 0.5f) > 0.2f
            bulbPaint.color = if (lit) Color.parseColor("#FFF6D8") else Color.parseColor("#6B5320")
            if (lit) bulbPaint.setShadowLayer(14f, 0f, 0f, Color.parseColor("#FFE79A"))
            else bulbPaint.clearShadowLayer()
            canvas.drawCircle(bx, by, rad, bulbPaint)
        }
        bulbPaint.clearShadowLayer()
    }

    private fun drawHub(canvas: Canvas) {
        val hr = radius * 0.16f
        hubPaint.shader = RadialGradient(
            cx, cy - hr * 0.4f, hr,
            intArrayOf(Color.parseColor("#222A52"), Color.parseColor("#0F1430")),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, hr, hubPaint)
        hubRingPaint.strokeWidth = hr * 0.16f
        canvas.drawCircle(cx, cy, hr, hubRingPaint)

        val pulse = if (spinning) 1f else 1f + sin(idlePhase * 4f) * 0.06f
        canvas.save()
        canvas.scale(pulse, pulse, cx, cy)
        hubTextPaint.textSize = hr * 0.42f
        val fm = hubTextPaint.fontMetrics
        canvas.drawText("SPIN", cx, cy - (fm.ascent + fm.descent) / 2f, hubTextPaint)
        canvas.restore()
    }

    private fun drawPointer(canvas: Canvas) {
        val topY = cy - (radius + radius * 0.072f)
        canvas.save()
        canvas.translate(cx, topY - radius * 0.02f)
        canvas.rotate(toDeg(flickAngle))
        val s = radius * 0.12f
        pointerPath.reset()
        pointerPath.moveTo(0f, s)                 // tip points down into the wheel
        pointerPath.lineTo(-s * 0.72f, -s * 0.7f)
        pointerPath.lineTo(s * 0.72f, -s * 0.7f)
        pointerPath.close()
        pointerPaint.shader = LinearGradient(
            0f, -s, 0f, s,
            Color.parseColor("#FFF0BF"), Color.parseColor("#E0A92E"),
            Shader.TileMode.CLAMP
        )
        pointerPaint.setShadowLayer(12f, 0f, 3f, Color.argb(115, 0, 0, 0))
        canvas.drawPath(pointerPath, pointerPaint)
        pointerPaint.clearShadowLayer()
        canvas.drawPath(pointerPath, pointerStroke)
        canvas.drawCircle(0f, -s * 0.7f, s * 0.32f, knobPaint)
        canvas.restore()
    }

    // ---- Confetti -----------------------------------------------------------

    private class Confetti(
        var x: Float, var y: Float, var vx: Float, var vy: Float,
        var rot: Float, var vr: Float, var w: Float, var h: Float,
        val color: Int, var life: Float, val circle: Boolean
    )

    private fun burstConfetti() {
        if (respectReducedMotion && isReducedMotion()) return
        val colors = segments.map { it.glow }
        repeat(130) {
            val ang = -HALF_PI + (Random.nextFloat() - 0.5f) * (PI.toFloat() * 1.1f)
            val sp = 6f + Random.nextFloat() * 9f
            confetti.add(
                Confetti(
                    x = cx, y = cy - radius * 0.1f,
                    vx = cos(ang) * sp * (0.5f + Random.nextFloat()),
                    vy = sin(ang) * sp - 3f,
                    rot = Random.nextFloat() * TWO_PI,
                    vr = (Random.nextFloat() - 0.5f) * 0.4f,
                    w = 7f + Random.nextFloat() * 7f,
                    h = 10f + Random.nextFloat() * 10f,
                    color = colors[Random.nextInt(colors.size)],
                    life = 1f,
                    circle = Random.nextFloat() < 0.4f
                )
            )
        }
    }

    private fun updateConfetti(dt: Float) {
        val g = 0.32f
        val it = confetti.iterator()
        while (it.hasNext()) {
            val p = it.next()
            p.vy += g * dt
            p.vx *= 0.992f
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.rot += p.vr * dt
            if (p.y > height + 40) it.remove()
        }
    }

    private fun drawConfetti(canvas: Canvas) {
        for (p in confetti) {
            canvas.save()
            canvas.translate(p.x, p.y)
            canvas.rotate(toDeg(p.rot))
            confettiPaint.color = p.color
            confettiPaint.alpha = (p.life.coerceIn(0f, 1f) * 255).toInt()
            if (p.circle) canvas.drawCircle(0f, 0f, p.w * 0.5f, confettiPaint)
            else canvas.drawRect(-p.w / 2, -p.h / 2, p.w / 2, p.h / 2, confettiPaint)
            canvas.restore()
        }
    }

    private fun clearConfetti() = confetti.clear()

    // ---- Input --------------------------------------------------------------

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && !spinning) {
            performClick()
            spin()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    // ---- Math helpers -------------------------------------------------------

    private fun segUnderPointer(rot: Float): Int {
        val a = mod(POINTER - rot, TWO_PI)
        return floor(a / seg).toInt().coerceIn(0, segments.size - 1)
    }

    private fun isReducedMotion(): Boolean {
        return try {
            android.provider.Settings.Global.getFloat(
                context.contentResolver,
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f
            ) == 0f
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val TWO_PI = (PI * 2).toFloat()
        private const val HALF_PI = (PI / 2).toFloat()
        private const val POINTER = -HALF_PI            // top, 12 o'clock

        private val EASE_OUT_QUART = TimeInterpolator { t -> (1f - (1f - t).pow(4)) }

        private fun mod(a: Float, m: Float): Float = ((a % m) + m) % m
        private fun toDeg(rad: Float): Float = rad * 180f / PI.toFloat()

        private fun shade(color: Int, amt: Int): Int {
            val r = (Color.red(color) + amt).coerceIn(0, 255)
            val g = (Color.green(color) + amt).coerceIn(0, 255)
            val b = (Color.blue(color) + amt).coerceIn(0, 255)
            return Color.rgb(r, g, b)
        }

        val DEFAULT_SEGMENTS = listOf(
            Segment("FREE DRINK", Color.parseColor("#E94B5A"), Color.parseColor("#FF7A86")),
            Segment("10% OFF", Color.parseColor("#F2A03D"), Color.parseColor("#FFC476")),
            Segment("SPIN AGAIN", Color.parseColor("#3FB6A8"), Color.parseColor("#74E7D9")),
            Segment("FREE COOKIE", Color.parseColor("#E9C44B"), Color.parseColor("#FFF0A0")),
            Segment("JACKPOT", Color.parseColor("#9B5DE5"), Color.parseColor("#C79BFF")),
            Segment("20% OFF", Color.parseColor("#3FA7E0"), Color.parseColor("#82D2FF")),
            Segment("FREE UPGRADE", Color.parseColor("#F15BA6"), Color.parseColor("#FF9BCF")),
            Segment("TRY AGAIN", Color.parseColor("#5A6BD8"), Color.parseColor("#94A3FF")),
        )
    }
}
