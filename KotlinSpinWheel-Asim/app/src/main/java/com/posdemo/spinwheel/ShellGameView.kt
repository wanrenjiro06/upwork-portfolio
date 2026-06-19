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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * ShellGameView
 * ----------------------------------------------------------------------------
 * A self-contained "Find the Ball" shell game for a customer-facing POS
 * display. Native Kotlin twin of the approved web demo.
 *
 *   REVEAL  → all cups lift to show which one hides the ball, then drop
 *   SHUFFLE → cups swap positions in quick arcs (speeding up)
 *   PICK    → the customer taps a cup
 *   LIFT    → the chosen cup lifts to reveal win/lose
 *   RESULT  → win = confetti + fanfare + prize
 *
 * PERFORMANCE MODEL (mirrors SpinWheelView's pre-rendered face): the cup and
 * the ball are pre-rendered once to Bitmaps; each frame only blits them at
 * computed positions and draws the cheap live bits (shadows, confetti).
 *
 * PRODUCTION HOOK (mirrors SpinWheelView.spinTo): the back office decides the
 * outcome (odds, stock, per-customer rules). The customer always gets to pick;
 * the reveal is set to the predetermined result:
 *
 *     shell.onResult = { won, prize -> if (won) pos.recordReward(prize) }
 *     shell.newRound(shouldWin = rewardEngine.decide(customer), prize = "Free Coffee")
 *
 * Calling [newRound] with no argument uses a weighted-random outcome (demo).
 */
class ShellGameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    // ---- Public API ---------------------------------------------------------

    /** Fired on the UI thread when a round resolves. */
    var onResult: ((won: Boolean, prize: String?) -> Unit)? = null

    /** Optional: receive the on-screen prompt text as the round progresses. */
    var onPrompt: ((String) -> Unit)? = null

    var soundEnabled: Boolean = true
    var respectReducedMotion: Boolean = true

    // ---- CONFIG (modular settings — edit freely) ----------------------------
    /** Cup colour. */
    var cupColor: Int = GameStyle.blue
    /** How many shuffle swaps per round (a small random 0–2 is added). */
    var shuffleCount: Int = 6
    /** Base time per shuffle swap, ms (before the global [GameStyle.speedFactor]). */
    var swapDurationMs: Float = 320f
    /** Demo-only win odds (0..1) when [newRound] is called with no outcome. */
    var demoWinChance: Float = 0.5f
    /** Prize labels picked at random when [newRound] is called with no prize. */
    var prizes: Array<String> = DEFAULT_PRIZES

    val roundState: String get() = state

    /** Start a round. [shouldWin] null = weighted-random demo outcome. */
    @JvmOverloads
    fun newRound(shouldWin: Boolean? = null, prize: String? = null) {
        won = false; pickedId = -1; revealOther = -1
        willWin = shouldWin ?: (Random.nextFloat() < demoWinChance)
        currentPrize = prize ?: prizes[Random.nextInt(prizes.size)]
        ballCup = Random.nextInt(3)
        for (c in cups) { c.slot = c.id; c.x = slotX[c.slot]; c.lift = 0f }
        swap = null; swapsLeft = 0
        setState(REVEAL); setPrompt("Watch closely…")
        invalidate()
    }

    // ---- State --------------------------------------------------------------

    private var state = IDLE
    private var stateT = 0f
    private var U = 0f

    private var cupW = 0f; private var cupH = 0f; private var ballR = 0f
    private var baseY = 0f; private var liftAmt = 0f; private var cupPad = 0f
    private val slotX = FloatArray(3)

    private var cupSprite: Bitmap? = null
    private var ballSprite: Bitmap? = null

    private class Cup(val id: Int) { var slot = id; var x = 0f; var lift = 0f; var z = 0 }
    private val cups = Array(3) { Cup(it) }

    private var ballCup = 0
    private var pickedId = -1
    private var revealOther = -1
    private var won = false
    private var willWin = true
    private var currentPrize = ""

    private class Swap(val ca: Cup, val cb: Cup, val fromA: Float, val toA: Float,
                       val fromB: Float, val toB: Float, val t0: Long, val dur: Float, val arc: Int)
    private var swap: Swap? = null
    private var swapsLeft = 0
    private var shuffleSpeed = 1f

    private val confetti = ArrayList<Conf>()

    // ---- Paints -------------------------------------------------------------

    private val spritePaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    private val tmp = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val confPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val promptPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER; color = Color.parseColor("#9AA8CF")
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }
    private val oval = RectF()
    private val path = Path()
    private val audio: ShellAudio by lazy { ShellAudio(context) }

    // ---- Loop ---------------------------------------------------------------

    private var lastFrameNs = 0L
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            step(frameTimeNanos)
            if (isAttachedToWindow) Choreographer.getInstance().postFrameCallback(this)
        }
    }
    override fun onAttachedToWindow() { super.onAttachedToWindow(); lastFrameNs = 0L; Choreographer.getInstance().postFrameCallback(frameCallback) }
    override fun onDetachedFromWindow() {
        Choreographer.getInstance().removeFrameCallback(frameCallback)
        cupSprite?.recycle(); cupSprite = null; ballSprite?.recycle(); ballSprite = null
        audio.release(); super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        U = min(w, h).toFloat()
        cupW = U * 0.22f; cupH = U * 0.30f; ballR = cupW * 0.22f; liftAmt = cupH * 0.9f
        baseY = h * 0.50f
        val spacing = cupW * 1.55f; val midX = w / 2f
        slotX[0] = midX - spacing; slotX[1] = midX; slotX[2] = midX + spacing
        buildSprites()
        for (c in cups) c.x = slotX[c.slot]
    }

    private fun setState(s: String) { state = s; stateT = 0f }
    private fun setPrompt(t: String) { onPrompt?.invoke(t); promptText = t }
    private var promptText = ""

    // ---- Sprites ------------------------------------------------------------

    private fun buildSprites() {
        cupSprite?.recycle(); ballSprite?.recycle()
        cupPad = U * 0.03f
        val pad = cupPad
        val w = cupW; val h = cupH
        val cup = Bitmap.createBitmap((w + pad * 2).toInt(), (h + pad * 2).toInt(), Bitmap.Config.ARGB_8888)
        val c = Canvas(cup)
        c.translate(pad, pad)
        path.reset()
        path.moveTo(w * 0.18f, 0f)
        path.quadTo(w * 0.5f, -h * 0.06f, w * 0.82f, 0f)
        path.lineTo(w * 0.98f, h * 0.92f)
        path.quadTo(w * 0.5f, h * 1.04f, w * 0.02f, h * 0.92f)
        path.close()
        tmp.shader = LinearGradient(0f, 0f, w, 0f,
            intArrayOf(shade(cupColor, -30), cupColor, shade(cupColor, -44)),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        c.drawPath(path, tmp); tmp.shader = null
        // sheen
        tmp.color = Color.argb(56, 255, 255, 255)
        oval.set(w * 0.36f - w * 0.07f, h * 0.5f - h * 0.4f, w * 0.36f + w * 0.07f, h * 0.5f + h * 0.4f)
        c.drawOval(oval, tmp)
        tmp.alpha = 255   // reset — shared paint's alpha must not bleed into later opaque fills
        // gold rim
        path.reset()
        path.moveTo(w * 0.02f, h * 0.92f); path.quadTo(w * 0.5f, h * 1.04f, w * 0.98f, h * 0.92f)
        path.lineTo(w * 0.98f, h * 0.86f); path.quadTo(w * 0.5f, h * 0.98f, w * 0.02f, h * 0.86f); path.close()
        tmp.shader = LinearGradient(0f, 0f, w, 0f,
            intArrayOf(Color.parseColor("#FFE9A8"), Color.parseColor("#C9962B"), Color.parseColor("#8A651A")),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        c.drawPath(path, tmp)
        // knob
        c.drawCircle(w * 0.5f, -h * 0.02f, w * 0.07f, tmp)
        tmp.shader = null
        cupSprite = cup

        val bs = (ballR * 2.4f).toInt().coerceAtLeast(2)
        val ball = Bitmap.createBitmap(bs, bs, Bitmap.Config.ARGB_8888)
        val b = Canvas(ball); val bc = bs / 2f
        tmp.alpha = 255   // ensure the ball fills fully opaque
        // Android RadialGradient is single-circle: keep the radius wider than the
        // ball so the far edge doesn't CLAMP to the dark stop (which made the ball
        // look brown). Highlight is the offset white circle below.
        tmp.shader = RadialGradient(bc - ballR * 0.25f, bc - ballR * 0.28f, ballR * 1.7f,
            intArrayOf(Color.parseColor("#FFFBEA"), Color.parseColor("#FFDD66"), Color.parseColor("#E6A92E")),
            floatArrayOf(0f, 0.6f, 1f), Shader.TileMode.CLAMP)
        b.drawCircle(bc, bc, ballR, tmp); tmp.shader = null
        tmp.color = Color.argb(153, 255, 255, 255); b.drawCircle(bc - ballR * 0.32f, bc - ballR * 0.36f, ballR * 0.22f, tmp)
        ballSprite = ball
    }

    // ---- Shuffle ------------------------------------------------------------

    private fun beginSwap() {
        val a = Random.nextInt(3); var b = Random.nextInt(3); if (b == a) b = (b + 1) % 3
        val ca = cups.first { it.slot == a }; val cb = cups.first { it.slot == b }
        val reduced = respectReducedMotion && isReducedMotion()
        swap = Swap(ca, cb, slotX[a], slotX[b], slotX[b], slotX[a], System.nanoTime(),
            (if (reduced) 420f else swapDurationMs) / shuffleSpeed / GameStyle.speedFactor, if (b > a) 1 else -1)
        if (soundEnabled) audio.whoosh()
    }
    private fun updateSwap(now: Long) {
        val s = swap ?: return
        val t = min((now - s.t0) / 1_000_000f / s.dur, 1f)
        val e = if (t < 0.5f) 2 * t * t else 1 - (-2 * t + 2).pow(2) / 2
        val arcY = -sin(t * PI.toFloat()) * cupH * 0.5f
        s.ca.x = s.fromA + (s.toA - s.fromA) * e; s.ca.lift = arcY * 0.4f; s.ca.z = if (s.arc > 0) 1 else 0
        s.cb.x = s.fromB + (s.toB - s.fromB) * e; s.cb.lift = arcY;        s.cb.z = if (s.arc > 0) 0 else 1
        if (t >= 1f) {
            val sa = s.ca.slot; s.ca.slot = s.cb.slot; s.cb.slot = sa
            s.ca.lift = 0f; s.cb.lift = 0f; s.ca.z = 0; s.cb.z = 0
            swap = null; swapsLeft--
        }
    }

    // ---- Pick ---------------------------------------------------------------

    private fun pickCup(id: Int) {
        if (state != PICK) return
        pickedId = id
        if (willWin) { ballCup = id; won = true }
        else { won = false; val other = (id + 1) % 3; ballCup = other; revealOther = other }
        setState(LIFT); setPrompt("")
        if (soundEnabled) audio.lift()
    }

    // ---- Per-frame ----------------------------------------------------------

    private fun step(nowNs: Long) {
        val dt = if (lastFrameNs == 0L) 1f else min((nowNs - lastFrameNs) / 16_666_667f, 3f)
        lastFrameNs = nowNs
        stateT += dt * 16.667f / 1000f

        when (state) {
            REVEAL -> {
                val tgt = if (stateT < 1.3f) liftAmt else 0f
                for (c in cups) c.lift += (tgt - c.lift) * min(1f, dt * 0.2f)
                if (stateT > 2.0f / GameStyle.speedFactor) {
                    swapsLeft = if (respectReducedMotion && isReducedMotion()) 4 else shuffleCount + Random.nextInt(3)
                    shuffleSpeed = 1f; setState(SHUFFLE); setPrompt("Keep your eye on the ball!")
                }
            }
            SHUFFLE -> {
                if (swap == null && swapsLeft > 0) { shuffleSpeed = min(1.8f, 1f + (8 - swapsLeft) * 0.12f); beginSwap() }
                swap?.let { updateSwap(nowNs) }
                for (c in cups) if (c !== swap?.ca && c !== swap?.cb) c.x = slotX[c.slot]
                if (swap == null && swapsLeft <= 0) { setState(PICK); setPrompt("Where is the ball? Tap a cup.") }
            }
            PICK -> for (c in cups) { c.x = slotX[c.slot]; c.lift += (0f - c.lift) * min(1f, dt * 0.2f) }
            LIFT -> {
                cups.firstOrNull { it.id == pickedId }?.let { it.lift += (liftAmt - it.lift) * min(1f, dt * 0.18f) }
                if (!won && stateT > 0.7f / GameStyle.speedFactor) cups.firstOrNull { it.id == revealOther }?.let { it.lift += (liftAmt - it.lift) * min(1f, dt * 0.16f) }
                if (stateT > 1.1f / GameStyle.speedFactor) {
                    setState(RESULT)
                    if (won) { burstConfetti(); if (soundEnabled) audio.fanfare() } else if (soundEnabled) audio.lose()
                    onResult?.let { cb -> val w = won; val p = if (won) currentPrize else null; post { cb(w, p) } }
                }
            }
        }

        if (confetti.isNotEmpty()) updateConfetti(dt)
        invalidate()
    }

    // ---- Draw ---------------------------------------------------------------

    override fun onDraw(canvas: Canvas) {
        if (U <= 0f || state == IDLE) return
        drawSurface(canvas)
        // ball (under its cup; visible when that cup is lifted, or during reveal)
        val ballCupObj = cups.first { it.id == ballCup }
        val ballVisible = state == REVEAL || (state != SHUFFLE && state != PICK && ballCupObj.lift > ballR * 0.5f)
        if (ballVisible) {
            ballSprite?.let {
                val bx = ballCupObj.x; val by = baseY + cupH - ballR * 0.9f
                canvas.drawBitmap(it, bx - it.width / 2f, by - it.height / 2f, spritePaint)
            }
        }
        // cups back-to-front
        val order = cups.sortedBy { it.z }
        for (c in order) cupSprite?.let { canvas.drawBitmap(it, c.x - cupW / 2f - cupPad, baseY - c.lift - cupPad, spritePaint) }
        if (confetti.isNotEmpty()) drawConfetti(canvas)
        if (promptText.isNotEmpty()) {
            promptPaint.textSize = maxOf(13f, U * 0.026f)
            canvas.drawText(promptText, width / 2f, height * 0.16f, promptPaint)
        }
    }

    private fun drawSurface(canvas: Canvas) {
        for (c in cups) {
            val k = min(c.lift / liftAmt, 1f)
            val r = cupW * 0.5f * (1f - k * 0.3f)
            shadowPaint.color = Color.argb(((0.4f - k * 0.22f) * 255).toInt().coerceIn(0, 255), 0, 0, 0)
            oval.set(c.x - r, baseY + cupH + U * 0.01f - U * 0.018f, c.x + r, baseY + cupH + U * 0.01f + U * 0.018f)
            canvas.drawOval(oval, shadowPaint)
        }
    }

    // ---- Confetti -----------------------------------------------------------

    private class Conf(var x: Float, var y: Float, var vx: Float, var vy: Float, var rot: Float, val vr: Float,
                       val w: Float, val h: Float, val color: Int, var wobble: Float)
    private fun burstConfetti() {
        if (respectReducedMotion && isReducedMotion()) return
        for (s in 0 until 2) {
            val x = if (s == 0) width * 0.05f else width * 0.95f; val y = height * 0.9f
            repeat(60) {
                val base = if (s == 0) -PI.toFloat() * 0.32f else -PI.toFloat() * 0.68f
                val ang = base + (Random.nextFloat() - 0.5f) * 0.7f
                val sp = U * (0.016f + Random.nextFloat() * 0.016f)
                confetti.add(Conf(x, y, cos(ang) * sp, sin(ang) * sp, Random.nextFloat() * TWO_PI, (Random.nextFloat() - 0.5f) * 0.5f,
                    U * (0.009f + Random.nextFloat() * 0.01f), U * (0.014f + Random.nextFloat() * 0.014f),
                    CONF_COLORS[Random.nextInt(CONF_COLORS.size)], Random.nextFloat() * TWO_PI))
            }
        }
    }
    private fun updateConfetti(dt: Float) {
        val g = U * 0.00055f
        val it = confetti.iterator()
        while (it.hasNext()) { val p = it.next()
            p.vy += g * dt * 16.67f; p.vx *= 0.992f; p.wobble += 0.2f * dt
            p.x += (p.vx + sin(p.wobble) * U * 0.0016f) * dt; p.y += p.vy * dt; p.rot += p.vr * dt
            if (p.y > height + 50) it.remove()
        }
    }
    private fun drawConfetti(canvas: Canvas) {
        for (p in confetti) {
            canvas.save(); canvas.translate(p.x, p.y); canvas.rotate(p.rot * 180f / PI.toFloat())
            confPaint.color = p.color; canvas.drawRect(-p.w / 2, -p.h / 2, p.w / 2, p.h / 2, confPaint); canvas.restore()
        }
    }

    // ---- Input --------------------------------------------------------------

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && state == PICK) {
            performClick()
            val px = event.x
            var best = -1; var bd = Float.MAX_VALUE
            for (c in cups) { val d = abs(px - c.x); if (d < bd) { bd = d; best = c.id } }
            if (best >= 0 && bd < cupW * 0.9f) { pickCup(best); return true }
        }
        return super.onTouchEvent(event)
    }
    override fun performClick(): Boolean { super.performClick(); return true }

    // ---- Helpers ------------------------------------------------------------

    private fun isReducedMotion(): Boolean = try {
        android.provider.Settings.Global.getFloat(context.contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    } catch (e: Exception) { false }

    companion object {
        private const val IDLE = "idle"
        private const val REVEAL = "reveal"
        private const val SHUFFLE = "shuffle"
        private const val PICK = "pick"
        private const val LIFT = "lift"
        private const val RESULT = "result"
        private const val TWO_PI = (PI * 2).toFloat()

        val DEFAULT_PRIZES = arrayOf("Free Drink", "Free Cookie", "10% Off", "Free Topping", "Free Upgrade", "20% Off")
        private val CONF_COLORS = intArrayOf(
            Color.parseColor("#82D2FF"), Color.parseColor("#3FA7E0"), Color.parseColor("#5A6BD8"),
            Color.WHITE, Color.parseColor("#FFD45E"), Color.parseColor("#22D3EE"), Color.parseColor("#FF9BCF")
        )
        private fun shade(color: Int, amt: Int): Int {
            val r = (Color.red(color) + amt).coerceIn(0, 255)
            val g = (Color.green(color) + amt).coerceIn(0, 255)
            val b = (Color.blue(color) + amt).coerceIn(0, 255)
            return Color.rgb(r, g, b)
        }
    }
}
