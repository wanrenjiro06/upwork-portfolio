package com.posdemo.spinwheel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
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
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * BirthdayCelebrationView
 * ----------------------------------------------------------------------------
 * A self-contained "Happy Birthday" celebration for a customer-facing POS
 * display. Native Kotlin twin of the approved web demo — same look, same beats:
 *
 *   INTRO  → headline kinetics + balloons rise + opening confetti + tune
 *   LIT    → candles flicker, balloons drift (the calm resting display)
 *   BLOWING→ tap blows the candles out left→right with smoke wisps
 *   FINALE → "Make a wish!" + confetti waves
 *   …back to LIT (relit) so the display stays alive and re-tappable.
 *
 * PERFORMANCE MODEL (mirrors SpinWheelView's pre-rendered face):
 *   Everything static is rendered ONCE to a Bitmap — the cake (plate, tiers,
 *   frosting, candle bodies), the balloon bodies, the sparkle glyph and the
 *   flame halo are all pre-rendered. Each frame only blits those bitmaps and
 *   draws the cheap live bits (flame paths, confetti, motion). No per-frame
 *   gradient allocation, no shadow layers in the loop → holds 60fps on a
 *   low-end POS tablet.
 *
 * PRODUCTION HOOK (mirrors SpinWheelView.spinTo):
 *   The POS decides WHEN (loyalty profile says it's the customer's birthday)
 *   and WHO (the name). The view just performs:
 *
 *     birthdayView.onComplete = { /* return to normal POS UI, log, etc. */ }
 *     birthdayView.celebrate(customerName)
 */
class BirthdayCelebrationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    // ---- Public API ---------------------------------------------------------

    /** Fired on the UI thread when the finale completes (one celebration cycle). */
    var onComplete: (() -> Unit)? = null

    /** Procedural music/SFX. Set false for silent kiosks. */
    var soundEnabled: Boolean = true

    /** Honors the device "remove animations" accessibility setting when true. */
    var respectReducedMotion: Boolean = true

    // ---- CONFIG (modular settings — edit freely) ----------------------------
    /** Number of candles on the cake. */
    var candleCount: Int = 5
    /** Cake colours (body + frosting) — change to restyle the cake. */
    var bottomTierColor: Int = GameStyle.indigo
    var bottomTierFrost: Int = Color.parseColor("#8C97EE")
    var topTierColor: Int = GameStyle.blue
    var topTierFrost: Int = GameStyle.blueLite
    /** Headline copy. The customer's name is set via [celebrate]. */
    var kickerText: String = "FROM ALL OF US AT THE SHOP"
    var titleText: String = "Happy Birthday"
    /** Balloon colour pairs [body, highlight]. Set before the view is sized. Global speed: GameStyle.speedFactor. */
    var balloonColors: Array<IntArray> = DEFAULT_BALLOON_COLORS

    /** Kick off a celebration for [name]. Safe to call again to replay. */
    fun celebrate(name: String) {
        customerName = name.trim().ifEmpty { "Friend" }
        seedBalloons(); seedSparkles(); confetti.clear()
        for (c in candles) { c.lit = true; c.flame = 1f; c.smoke.clear() }
        headTarget = 1f; wishTarget = 0f
        setState(ST_INTRO)
        if (soundEnabled) audio.tune()
        burst(width / 2f, height * 0.42f, 90)
        invalidate()
    }

    /** Blow the candles out (what a tap does in LIT/INTRO). */
    fun blow() {
        if (state != ST_LIT && state != ST_INTRO) return
        setState(ST_BLOWING)
        if (soundEnabled) audio.poof()
    }

    // ---- State machine ------------------------------------------------------

    private var state = ST_IDLE
    private var stateT = 0f                 // seconds in current state
    private var customerName = "Friend"
    private fun setState(s: Int) { state = s; stateT = 0f }

    // ---- Geometry -----------------------------------------------------------

    private var U = 0f
    private var cakeFace: Bitmap? = null
    private var faceDirty = true

    private val balloonSprites = HashMap<Int, Bitmap>()
    private var sparkleSprite: Bitmap? = null
    private var haloSprite: Bitmap? = null

    // cake metrics
    private var cakeCx = 0f
    private var plateY = 0f
    private var candleW = 0f
    private var candleH = 0f
    private var topTierTopY = 0f

    // ---- Live actors --------------------------------------------------------

    private val candles = ArrayList<Candle>()
    private val balloons = ArrayList<Balloon>()
    private val sparkles = ArrayList<Sparkle>()
    private val confetti = ArrayList<Conf>()

    private var headAlpha = 0f; private var headTarget = 0f
    private var wishAlpha = 0f; private var wishTarget = 0f
    private var confTimer = 0f

    // ---- Paints (allocated once) --------------------------------------------

    private val tmpPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val flamePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val corePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(217, 120, 180, 255) }
    private val innerFlamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFF6D8") }
    private val smokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#CFD6E8") }
    private val confPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = Color.argb(72, 255, 255, 255)
    }
    private val spritePaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)

    private val kickerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER; color = Color.parseColor("#82D2FF")
        letterSpacing = 0.3f; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    private val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    private val wishPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    private val wishSubPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER; color = Color.parseColor("#9AA8CF")
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val oval = RectF()
    private val path = Path()
    private val audio: BirthdayAudio by lazy { BirthdayAudio(context) }

    // ---- Loop ---------------------------------------------------------------

    private var lastFrameNs = 0L
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
        recycleSprites()
        audio.release()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        U = min(w, h).toFloat()
        cakeCx = w / 2f
        plateY = h * 0.80f
        candleW = maxOf(7f, U * 0.016f)
        candleH = U * 0.105f
        faceDirty = true
        layoutCandles()
        buildSprites()
    }

    // ---- Per-frame ----------------------------------------------------------

    private fun step(nowNs: Long) {
        val dt = if (lastFrameNs == 0L) 1f else min((nowNs - lastFrameNs) / 16_666_667f, 3f)
        lastFrameNs = nowNs
        val dtSec = dt * 16.667f / 1000f
        stateT += dtSec

        if (state == ST_INTRO && stateT > 2.2f / GameStyle.speedFactor) setState(ST_LIT)

        if (state == ST_BLOWING) {
            val order = stateT * GameStyle.speedFactor / 0.12f
            candles.forEachIndexed { i, c ->
                if (c.lit && i <= order) {
                    c.lit = false
                    repeat(6) {
                        c.smoke.add(Smoke(
                            (Random.nextFloat() - 0.5f) * candleW, 0f,
                            (Random.nextFloat() - 0.5f) * 0.3f, -(0.3f + Random.nextFloat() * 0.5f),
                            candleW * (0.3f + Random.nextFloat() * 0.3f), 1f
                        ))
                    }
                }
            }
            if (stateT > 0.9f / GameStyle.speedFactor && candles.all { !it.lit }) {
                setState(ST_FINALE)
                headTarget = 0f; wishTarget = 1f
                if (soundEnabled) audio.tada()
                sideCannons(); burst(width / 2f, height * 0.45f, 80); confTimer = 0.45f
            }
        }

        if (state == ST_FINALE) {
            confTimer -= dtSec
            if (confTimer <= 0f && stateT < 3.2f) {
                burst(width * (0.3f + Random.nextFloat() * 0.4f), height * 0.5f, 26); confTimer = 0.42f
            }
            if (stateT > 4.0f / GameStyle.speedFactor) {
                setState(ST_LIT)
                for (c in candles) c.lit = true
                headTarget = 1f; wishTarget = 0f
                onComplete?.let { post { it() } }
            }
        }

        // candle flicker + flame easing + smoke
        for (c in candles) {
            c.flick += dt * (0.25f + Random.nextFloat() * 0.05f)
            val target = if (c.lit) 1f else 0f
            c.flame += (target - c.flame) * min(1f, dt * 0.35f)
            if (c.smoke.isNotEmpty()) {
                val it = c.smoke.iterator()
                while (it.hasNext()) {
                    val p = it.next()
                    p.y += p.vy * dt; p.x += p.vx * dt; p.r += dt * 0.4f; p.life -= dt * 0.03f
                    if (p.life <= 0f) it.remove()
                }
            }
        }

        // text alphas
        headAlpha += (headTarget - headAlpha) * min(1f, dt * 0.12f)
        wishAlpha += (wishTarget - wishAlpha) * min(1f, dt * 0.12f)

        if (state != ST_IDLE) updateBalloons(dt)
        if (confetti.isNotEmpty()) updateConfetti(dt)
        for (s in sparkles) s.phase += s.speed * dt * 0.04f

        invalidate()
    }

    // ---- Draw ---------------------------------------------------------------

    override fun onDraw(canvas: Canvas) {
        if (U <= 0f || state == ST_IDLE) return
        if (faceDirty || cakeFace == null) renderCakeFace()

        drawSparkles(canvas)
        for (b in balloons) drawBalloon(canvas, b)

        cakeFace?.let { canvas.drawBitmap(it, 0f, 0f, spritePaint) }
        for (c in candles) {
            if (c.flame > 0.02f) drawFlame(canvas, c)
            if (c.smoke.isNotEmpty()) {
                val wickY = c.baseY - c.h
                for (p in c.smoke) {
                    smokePaint.alpha = (p.life * 102f).toInt().coerceIn(0, 255)
                    canvas.drawCircle(c.x + p.x, wickY + p.y, p.r, smokePaint)
                }
            }
        }

        if (confetti.isNotEmpty()) drawConfetti(canvas)
        drawText(canvas)
    }

    // ---- Cake pre-render ----------------------------------------------------

    private fun layoutCandles() {
        candles.clear()
        val baseW = U * 0.40f
        val tierH = U * 0.115f
        val topW = baseW * 0.66f
        topTierTopY = plateY - tierH - tierH
        val spread = topW * 0.72f
        val cols = intArrayOf(
            Color.parseColor("#82D2FF"), Color.WHITE, Color.parseColor("#5A6BD8"), Color.WHITE, Color.parseColor("#82D2FF")
        )
        val nCandles = candleCount.coerceAtLeast(1)
        for (i in 0 until nCandles) {
            val fx = if (nCandles == 1) 0f else (i / (nCandles - 1f) - 0.5f)
            candles.add(Candle(cakeCx + fx * spread, topTierTopY, candleH, cols[i % cols.size]))
        }
    }

    private fun renderCakeFace() {
        cakeFace?.recycle()
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        tmpPaint.alpha = 255   // shared paint — clear any alpha left by the balloon sprites
        val baseW = U * 0.40f
        val tierH = U * 0.115f
        val plateW = baseW * 1.5f

        // plate soft shadow (radial — no blur filter)
        tmpPaint.shader = RadialGradient(
            cakeCx, plateY + U * 0.02f, plateW * 0.6f,
            intArrayOf(Color.argb(102, 0, 0, 0), Color.argb(0, 0, 0, 0)),
            floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )
        c.drawRect(cakeCx - plateW * 0.7f, plateY - U * 0.04f, cakeCx + plateW * 0.7f, plateY + U * 0.10f, tmpPaint)
        tmpPaint.shader = null

        // plate
        oval.set(cakeCx - plateW * 0.5f, plateY - U * 0.026f, cakeCx + plateW * 0.5f, plateY + U * 0.026f)
        tmpPaint.shader = LinearGradient(
            cakeCx - plateW * 0.5f, 0f, cakeCx + plateW * 0.5f, 0f,
            intArrayOf(Color.parseColor("#CDD6F2"), Color.parseColor("#EEF3FF"), Color.parseColor("#AAB6DA")),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP
        )
        c.drawOval(oval, tmpPaint)
        tmpPaint.shader = null

        // tiers bottom-up
        drawTier(c, plateY, plateY - tierH, baseW, bottomTierColor, bottomTierFrost)
        drawTier(c, plateY - tierH, plateY - 2 * tierH, baseW * 0.66f, topTierColor, topTierFrost)

        // candle bodies
        for (cd in candles) drawCandleBody(c, cd)

        cakeFace = bmp
        faceDirty = false
    }

    private fun drawTier(c: Canvas, baseY: Float, topY: Float, w: Float, color: Int, frost: Int) {
        tmpPaint.alpha = 255   // prior tier's top-highlight left alpha < 255 on the shared paint
        val halfW = w * 0.5f
        val ry = U * 0.022f

        path.reset()
        path.moveTo(cakeCx - halfW, topY)
        path.lineTo(cakeCx - halfW, baseY)
        oval.set(cakeCx - halfW, baseY - ry, cakeCx + halfW, baseY + ry)
        path.arcTo(oval, 180f, -180f)
        path.lineTo(cakeCx + halfW, topY)
        path.close()
        tmpPaint.shader = LinearGradient(
            cakeCx - halfW, 0f, cakeCx + halfW, 0f,
            intArrayOf(shade(color, -26), color, shade(color, -40)),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP
        )
        c.drawPath(path, tmpPaint)
        tmpPaint.shader = null

        drawSprinkles(c, topY + U * 0.03f, halfW, baseY - topY - U * 0.05f, w)

        // frosting cap
        oval.set(cakeCx - halfW, topY - ry, cakeCx + halfW, topY + ry)
        tmpPaint.shader = LinearGradient(
            0f, topY - ry, 0f, topY + ry,
            intArrayOf(Color.WHITE, frost), null, Shader.TileMode.CLAMP
        )
        c.drawOval(oval, tmpPaint)
        tmpPaint.shader = null

        // drip skirt
        path.reset()
        path.moveTo(cakeCx - halfW, topY)
        val drips = 7
        for (d in 0..drips) {
            val dx = cakeCx - halfW + (d.toFloat() / drips) * (halfW * 2)
            val depth = (sin(d * 1.7f) * 0.5f + 0.6f) * U * 0.03f
            path.quadTo(dx, topY + depth + ry * 0.4f, dx + (halfW * 2 / drips) * 0.5f, topY + ry * 0.2f)
        }
        path.lineTo(cakeCx + halfW, topY)
        oval.set(cakeCx - halfW, topY - ry, cakeCx + halfW, topY + ry)
        path.arcTo(oval, 0f, 180f)
        path.close()
        tmpPaint.color = frost; tmpPaint.alpha = 245
        c.drawPath(path, tmpPaint); tmpPaint.alpha = 255

        // top highlight
        oval.set(cakeCx - halfW, topY - ry, cakeCx + halfW, topY + ry)
        tmpPaint.color = Color.argb(46, 255, 255, 255)
        c.drawOval(oval, tmpPaint)
    }

    private fun drawSprinkles(c: Canvas, top: Float, halfW: Float, h: Float, w: Float) {
        val seed = (w * 13f) % 97f
        val cols = intArrayOf(Color.WHITE, Color.parseColor("#FFD45E"), Color.parseColor("#FF9BCF"), Color.parseColor("#82D2FF"))
        for (i in 0 until 26) {
            val rx = (pseudo(seed + i * 1.7f) - 0.5f) * 2f * halfW * 0.86f
            val ry = pseudo(seed + i * 2.3f) * h
            c.save()
            c.translate(cakeCx + rx, top + ry)
            c.rotate(pseudo(seed + i) * 360f)
            tmpPaint.color = cols[i % 4]; tmpPaint.alpha = 230
            c.drawRect(-U * 0.004f, -U * 0.0014f, U * 0.004f, U * 0.0014f, tmpPaint)
            c.restore()
        }
        tmpPaint.alpha = 255
    }

    private fun drawCandleBody(c: Canvas, cd: Candle) {
        tmpPaint.alpha = 255
        val w = candleW
        val topY = cd.baseY - cd.h
        c.save()
        oval.set(cd.x - w / 2, topY, cd.x + w / 2, cd.baseY)
        path.reset(); path.addRoundRect(oval, w * 0.25f, w * 0.25f, Path.Direction.CW)
        c.clipPath(path)
        tmpPaint.color = Color.WHITE
        c.drawRect(cd.x - w / 2, topY, cd.x + w / 2, cd.baseY, tmpPaint)
        tmpPaint.color = cd.color; tmpPaint.strokeWidth = w * 0.5f; tmpPaint.style = Paint.Style.STROKE
        var yy = -cd.h
        while (yy < cd.h) {
            c.drawLine(cd.x - w, topY + yy, cd.x + w, topY + yy + w * 1.6f, tmpPaint)
            yy += w * 1.1f
        }
        tmpPaint.style = Paint.Style.FILL
        c.restore()
        // wick
        tmpPaint.color = Color.parseColor("#3A2A1A"); tmpPaint.strokeWidth = maxOf(1.4f, w * 0.16f); tmpPaint.style = Paint.Style.STROKE
        c.drawLine(cd.x, topY, cd.x, topY - w * 0.6f, tmpPaint)
        tmpPaint.style = Paint.Style.FILL
    }

    // ---- Sprites ------------------------------------------------------------

    private fun buildSprites() {
        recycleSprites()
        // balloons
        for (i in balloonColors.indices) {
            val pair = balloonColors[i]
            val bmp = Bitmap.createBitmap((BR * 2).toInt(), (BR * 2.6f).toInt(), Bitmap.Config.ARGB_8888)
            val c = Canvas(bmp)
            tmpPaint.alpha = 255   // clear the prior balloon's highlight alpha so this body is opaque
            val ox = BR; val oy = BR * 1.02f
            tmpPaint.shader = RadialGradient(
                ox - BR * 0.32f, oy - BR * 0.42f, BR * 1.25f,
                intArrayOf(pair[1], pair[0], shade(pair[0], -34)),
                floatArrayOf(0f, 0.55f, 1f), Shader.TileMode.CLAMP
            )
            oval.set(ox - BR * 0.86f, oy - BR, ox + BR * 0.86f, oy + BR)
            c.drawOval(oval, tmpPaint)
            tmpPaint.shader = null
            // knot
            path.reset(); path.moveTo(ox - BR * 0.1f, oy + BR * 0.98f); path.lineTo(ox + BR * 0.1f, oy + BR * 0.98f); path.lineTo(ox, oy + BR * 1.14f); path.close()
            tmpPaint.color = shade(pair[0], -34); c.drawPath(path, tmpPaint)
            // highlight
            tmpPaint.color = Color.argb(107, 255, 255, 255)
            c.save(); c.translate(ox - BR * 0.3f, oy - BR * 0.36f); c.rotate(-28f)
            oval.set(-BR * 0.2f, -BR * 0.32f, BR * 0.2f, BR * 0.32f); c.drawOval(oval, tmpPaint); c.restore()
            balloonSprites[i] = bmp
        }
        // sparkle
        val sz = 96; val c0 = sz / 2f
        val sp = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888)
        val sc = Canvas(sp)
        tmpPaint.alpha = 255
        tmpPaint.shader = RadialGradient(c0, c0, c0,
            intArrayOf(Color.argb(217, 170, 220, 255), Color.argb(64, 120, 180, 255), Color.argb(0, 120, 180, 255)),
            floatArrayOf(0f, 0.4f, 1f), Shader.TileMode.CLAMP)
        sc.drawCircle(c0, c0, c0, tmpPaint); tmpPaint.shader = null
        val r = sz * 0.42f
        path.reset(); path.moveTo(c0, c0 - r); path.quadTo(c0, c0, c0 + r, c0); path.quadTo(c0, c0, c0, c0 + r)
        path.quadTo(c0, c0, c0 - r, c0); path.quadTo(c0, c0, c0, c0 - r); path.close()
        tmpPaint.color = Color.parseColor("#EAF6FF"); sc.drawPath(path, tmpPaint)
        sparkleSprite = sp
        // halo
        val hsz = 160; val h0 = hsz / 2f
        val hb = Bitmap.createBitmap(hsz, hsz, Bitmap.Config.ARGB_8888)
        val hc = Canvas(hb)
        tmpPaint.shader = RadialGradient(h0, h0, h0,
            intArrayOf(Color.argb(153, 255, 222, 130), Color.argb(46, 255, 150, 60), Color.argb(0, 255, 150, 60)),
            floatArrayOf(0f, 0.4f, 1f), Shader.TileMode.CLAMP)
        hc.drawCircle(h0, h0, h0, tmpPaint); tmpPaint.shader = null
        haloSprite = hb
    }

    private fun recycleSprites() {
        balloonSprites.values.forEach { it.recycle() }; balloonSprites.clear()
        sparkleSprite?.recycle(); sparkleSprite = null
        haloSprite?.recycle(); haloSprite = null
        cakeFace?.recycle(); cakeFace = null
        faceDirty = true
    }

    // ---- Balloons -----------------------------------------------------------

    private fun seedBalloons() {
        balloons.clear()
        val count = if (width > 900) 10 else 7
        repeat(count) { spawnBalloon(true) }
    }
    private fun spawnBalloon(initial: Boolean) {
        val r = U * (0.05f + Random.nextFloat() * 0.035f)
        val idx = Random.nextInt(balloonColors.size)
        balloons.add(Balloon(
            x = width * (0.06f + Random.nextFloat() * 0.88f),
            y = if (initial) height * (0.2f + Random.nextFloat() * 0.95f) else height + r * 2.4f,
            r = r, vy = U * (0.00075f + Random.nextFloat() * 0.0007f),
            sway = Random.nextFloat() * TWO_PI, swaySpeed = 0.6f + Random.nextFloat() * 0.7f,
            swayAmp = r * (0.5f + Random.nextFloat() * 0.5f), idx = idx,
            tilt = (Random.nextFloat() - 0.5f) * 0.25f
        ))
    }
    private fun updateBalloons(dt: Float) {
        for (b in balloons) {
            b.y -= b.vy * dt * 16.67f
            b.sway += b.swaySpeed * dt * 0.02f
            if (b.y < -b.r * 3) { b.y = height + b.r * 2.6f; b.x = width * (0.06f + Random.nextFloat() * 0.88f) }
        }
    }
    private fun drawBalloon(canvas: Canvas, b: Balloon) {
        val x = b.x + sin(b.sway) * b.swayAmp
        val y = b.y; val r = b.r
        canvas.save()
        canvas.translate(x, y)
        canvas.rotate((sin(b.sway) * 0.08f + b.tilt) * 180f / PI.toFloat())
        // string
        stringPaint.strokeWidth = maxOf(1f, r * 0.03f)
        path.reset(); path.moveTo(0f, r * 1.04f)
        path.quadTo(r * 0.18f, r * 1.6f, -r * 0.08f, r * 2.3f)
        path.quadTo(-r * 0.3f, r * 2.9f, r * 0.04f, r * 3.5f)
        canvas.drawPath(path, stringPaint)
        // body sprite
        val scale = r / BR
        canvas.scale(scale, scale)
        balloonSprites[b.idx]?.let { canvas.drawBitmap(it, -BR, -BR * 1.02f, spritePaint) }
        canvas.restore()
    }

    // ---- Sparkles -----------------------------------------------------------

    private fun seedSparkles() {
        sparkles.clear()
        val n = if (width > 900) 24 else 15
        repeat(n) {
            sparkles.add(Sparkle(
                Random.nextFloat() * width, Random.nextFloat() * height * 0.7f,
                U * (0.03f + Random.nextFloat() * 0.05f), Random.nextFloat() * TWO_PI, 0.6f + Random.nextFloat() * 1.4f
            ))
        }
    }
    private fun drawSparkles(canvas: Canvas) {
        val sp = sparkleSprite ?: return
        for (s in sparkles) {
            val tw = sin(s.phase) * 0.5f + 0.5f
            if (tw < 0.06f) continue
            val d = s.size * (0.5f + tw)
            spritePaint.alpha = (tw * 230f).toInt().coerceIn(0, 255)
            oval.set(s.x - d / 2, s.y - d / 2, s.x + d / 2, s.y + d / 2)
            canvas.drawBitmap(sp, null, oval, spritePaint)
        }
        spritePaint.alpha = 255
    }

    // ---- Flames -------------------------------------------------------------

    private fun drawFlame(canvas: Canvas, c: Candle) {
        val w = candleW
        val wickY = c.baseY - c.h        // flame base sits ON the candle top (wick tip hidden inside the flame)
        val flick = sin(c.flick) * 0.12f + sin(c.flick * 2.3f) * 0.05f
        val sx = (1f + flick) * c.flame
        val hgt = w * 3.0f * c.flame * (1f + flick * 0.4f)
        val bend = sin(c.flick * 1.3f) * w * 0.25f * c.flame
        val fx = c.x + bend; val fy = wickY

        // halo
        val hs = hgt * 3.6f
        haloSprite?.let {
            spritePaint.alpha = (c.flame * 230f).toInt().coerceIn(0, 255)
            oval.set(fx - hs / 2, fy - hgt * 0.4f - hs / 2, fx + hs / 2, fy - hgt * 0.4f + hs / 2)
            canvas.drawBitmap(it, null, oval, spritePaint)
        }
        spritePaint.alpha = 255

        // outer flame
        path.reset(); path.moveTo(fx, fy)
        path.cubicTo(fx - w * 0.7f * sx, fy - hgt * 0.35f, fx - w * 0.45f * sx, fy - hgt * 0.85f, fx, fy - hgt)
        path.cubicTo(fx + w * 0.45f * sx, fy - hgt * 0.85f, fx + w * 0.7f * sx, fy - hgt * 0.35f, fx, fy)
        flamePaint.shader = LinearGradient(fx, fy, fx, fy - hgt,
            intArrayOf(Color.parseColor("#FF7A1A"), Color.parseColor("#FFB23E"), Color.parseColor("#FFE89A")),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        canvas.drawPath(path, flamePaint)
        flamePaint.shader = null

        // inner flame
        val ih = hgt * 0.58f
        path.reset(); path.moveTo(fx, fy - hgt * 0.06f)
        path.cubicTo(fx - w * 0.32f * sx, fy - ih * 0.4f, fx - w * 0.2f * sx, fy - ih * 0.85f, fx, fy - ih)
        path.cubicTo(fx + w * 0.2f * sx, fy - ih * 0.85f, fx + w * 0.32f * sx, fy - ih * 0.4f, fx, fy - hgt * 0.06f)
        canvas.drawPath(path, innerFlamePaint)

        // blue core
        oval.set(fx - w * 0.28f * sx, fy - hgt * 0.08f - hgt * 0.14f, fx + w * 0.28f * sx, fy - hgt * 0.08f + hgt * 0.14f)
        canvas.drawOval(oval, corePaint)
    }

    // ---- Confetti -----------------------------------------------------------

    private fun pushPiece(x: Float, y: Float, vx: Float, vy: Float) {
        confetti.add(Conf(x, y, vx, vy, Random.nextFloat() * TWO_PI, (Random.nextFloat() - 0.5f) * 0.45f,
            U * (0.009f + Random.nextFloat() * 0.01f), U * (0.013f + Random.nextFloat() * 0.014f),
            CONF_COLORS[Random.nextInt(CONF_COLORS.size)], Random.nextFloat() < 0.5f, Random.nextFloat() * TWO_PI))
    }
    private fun burst(x: Float, y: Float, amount: Int) {
        if (respectReducedMotion && isReducedMotion()) return
        repeat(amount) {
            val ang = -HALF_PI + (Random.nextFloat() - 0.5f) * (PI.toFloat() * 1.15f)
            val sp = (U * 0.012f) * (0.5f + Random.nextFloat() * 1.1f)
            pushPiece(x, y, cos(ang) * sp, sin(ang) * sp - U * 0.006f)
        }
    }
    private fun sideCannons() {
        if (respectReducedMotion && isReducedMotion()) return
        for (s in 0 until 2) {
            val x = if (s == 0) width * 0.04f else width * 0.96f; val y = height * 0.92f
            repeat(64) {
                val base = if (s == 0) -PI.toFloat() * 0.32f else -PI.toFloat() * 0.68f
                val ang = base + (Random.nextFloat() - 0.5f) * 0.7f
                val sp = U * (0.016f + Random.nextFloat() * 0.016f)
                pushPiece(x, y, cos(ang) * sp, sin(ang) * sp)
            }
        }
    }
    private fun updateConfetti(dt: Float) {
        val g = U * 0.00055f
        val it = confetti.iterator()
        while (it.hasNext()) {
            val p = it.next()
            p.vy += g * dt * 16.67f; p.vx *= 0.992f; p.wobble += 0.2f * dt
            p.x += (p.vx + sin(p.wobble) * U * 0.0016f) * dt; p.y += p.vy * dt; p.rot += p.vr * dt
            if (p.y > height + 50) it.remove()
        }
    }
    private fun drawConfetti(canvas: Canvas) {
        for (p in confetti) {
            canvas.save(); canvas.translate(p.x, p.y); canvas.rotate(p.rot * 180f / PI.toFloat())
            confPaint.color = p.color
            if (p.ribbon) { oval.set(-p.w * 0.5f, -p.h * 0.5f, p.w * 0.5f, p.h * 0.5f); canvas.drawOval(oval, confPaint) }
            else canvas.drawRect(-p.w / 2, -p.h / 2, p.w / 2, p.h / 2, confPaint)
            canvas.restore()
        }
    }

    // ---- Text ---------------------------------------------------------------

    private fun drawText(canvas: Canvas) {
        val topY = maxOf(U * 0.07f, height * 0.07f)

        // headline group (kicker + Happy Birthday + name)
        if (headAlpha > 0.01f) {
            val a = headAlpha
            kickerPaint.textSize = maxOf(11f, U * 0.018f)
            kickerPaint.alpha = (a * 242f).toInt().coerceIn(0, 255)
            canvas.drawText(kickerText, cakeCx, topY, kickerPaint)

            val titleSize = minOf(U * 0.115f, width * 0.082f)
            titlePaint.textSize = titleSize
            titlePaint.shader = LinearGradient(0f, topY + titleSize * 0.2f, 0f, topY + titleSize * 1.2f,
                intArrayOf(Color.WHITE, Color.parseColor("#82D2FF"), Color.parseColor("#3FA7E0")),
                floatArrayOf(0f, 0.58f, 1f), Shader.TileMode.CLAMP)
            titlePaint.alpha = (a * 255f).toInt().coerceIn(0, 255)
            val titleY = topY + titleSize * 1.05f
            canvas.drawText(titleText, cakeCx, titleY, titlePaint)
            titlePaint.shader = null

            val nameSize = titleSize * 0.78f
            namePaint.textSize = nameSize
            namePaint.shader = LinearGradient(0f, titleY + nameSize * 0.1f, 0f, titleY + nameSize,
                intArrayOf(Color.parseColor("#FFE9A8"), Color.parseColor("#FFD45E"), Color.parseColor("#F2A03D")),
                floatArrayOf(0f, 0.6f, 1f), Shader.TileMode.CLAMP)
            namePaint.alpha = (a * 255f).toInt().coerceIn(0, 255)
            canvas.drawText(customerName, cakeCx, titleY + nameSize * 1.05f, namePaint)
            namePaint.shader = null
        }

        // wish group
        if (wishAlpha > 0.01f) {
            val a = wishAlpha
            val wishSize = minOf(U * 0.10f, width * 0.075f)
            wishPaint.textSize = wishSize
            wishPaint.shader = LinearGradient(0f, topY, 0f, topY + wishSize,
                intArrayOf(Color.WHITE, Color.parseColor("#82D2FF"), Color.parseColor("#3FA7E0")),
                floatArrayOf(0f, 0.6f, 1f), Shader.TileMode.CLAMP)
            wishPaint.alpha = (a * 255f).toInt().coerceIn(0, 255)
            val wy = topY + wishSize
            canvas.drawText("Make a wish!", cakeCx, wy, wishPaint)
            wishPaint.shader = null

            wishSubPaint.textSize = maxOf(13f, U * 0.024f)
            wishSubPaint.alpha = (a * 255f).toInt().coerceIn(0, 255)
            canvas.drawText("Wishing you the happiest of birthdays", cakeCx, wy + wishSize * 0.5f, wishSubPaint)
        }
    }

    // ---- Input --------------------------------------------------------------

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            performClick()
            when (state) {
                ST_LIT, ST_INTRO -> blow()
                ST_IDLE -> celebrate(customerName)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean { super.performClick(); return true }

    // ---- Helpers ------------------------------------------------------------

    private fun isReducedMotion(): Boolean = try {
        android.provider.Settings.Global.getFloat(
            context.contentResolver, android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f
        ) == 0f
    } catch (e: Exception) { false }

    // ---- Data ---------------------------------------------------------------

    private class Candle(val x: Float, val baseY: Float, val h: Float, val color: Int) {
        var lit = true; var flame = 1f; var flick = Random.nextFloat() * TWO_PI
        val smoke = ArrayList<Smoke>()
    }
    private class Smoke(var x: Float, var y: Float, var vx: Float, var vy: Float, var r: Float, var life: Float)
    private class Balloon(
        var x: Float, var y: Float, val r: Float, val vy: Float,
        var sway: Float, val swaySpeed: Float, val swayAmp: Float, val idx: Int, val tilt: Float
    )
    private class Sparkle(val x: Float, val y: Float, val size: Float, var phase: Float, val speed: Float)
    private class Conf(
        var x: Float, var y: Float, var vx: Float, var vy: Float, var rot: Float, val vr: Float,
        val w: Float, val h: Float, val color: Int, val ribbon: Boolean, var wobble: Float
    )

    companion object {
        private const val ST_IDLE = 0
        private const val ST_INTRO = 1
        private const val ST_LIT = 2
        private const val ST_BLOWING = 3
        private const val ST_FINALE = 4

        private const val TWO_PI = (PI * 2).toFloat()
        private const val HALF_PI = (PI / 2).toFloat()
        private const val BR = 120f

        val DEFAULT_BALLOON_COLORS = arrayOf(
            intArrayOf(Color.parseColor("#3FA7E0"), Color.parseColor("#82D2FF")),
            intArrayOf(Color.parseColor("#5A6BD8"), Color.parseColor("#94A3FF")),
            intArrayOf(Color.parseColor("#E8EEFF"), Color.WHITE),
            intArrayOf(Color.parseColor("#3FB6A8"), Color.parseColor("#74E7D9")),
            intArrayOf(Color.parseColor("#F2A03D"), Color.parseColor("#FFC476")),
        )
        private val CONF_COLORS = intArrayOf(
            Color.parseColor("#82D2FF"), Color.parseColor("#3FA7E0"), Color.parseColor("#5A6BD8"),
            Color.WHITE, Color.parseColor("#FFD45E"), Color.parseColor("#74E7D9"), Color.parseColor("#FF9BCF")
        )

        private fun shade(color: Int, amt: Int): Int {
            val r = (Color.red(color) + amt).coerceIn(0, 255)
            val g = (Color.green(color) + amt).coerceIn(0, 255)
            val b = (Color.blue(color) + amt).coerceIn(0, 255)
            return Color.rgb(r, g, b)
        }
        private fun pseudo(x: Float): Float { val s = sin(x * 127.1f) * 43758.5453f; return s - kotlin.math.floor(s) }
    }
}
