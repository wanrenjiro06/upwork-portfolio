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
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * SlotMachineView
 * ----------------------------------------------------------------------------
 * A self-contained "Lucky Spin" slot machine for a customer-facing POS display.
 * Native Kotlin twin of the approved web demo — same cabinet, reels, symbols,
 * lever, lights and win celebration.
 *
 * PERFORMANCE MODEL (mirrors SpinWheelView's pre-rendered face):
 *   The cabinet and every symbol TILE are pre-rendered once to Bitmaps. Each
 *   frame only blits the visible symbol tiles per reel and draws the cheap live
 *   bits (lever, chase lights, confetti). No per-frame gradient allocation in
 *   the loop → holds 60fps on a low-end POS tablet.
 *
 * PRODUCTION HOOK (mirrors SpinWheelView.spinTo):
 *   The back office decides the outcome (odds, stock, per-customer rules):
 *
 *     slots.onResult = { symbols, isWin, prize -> /* record redemption */ }
 *     slots.spinTo(intArrayOf(s0, s1, s2))   // land these symbol indices on the payline
 *
 *   Use [spin] (random, demo-weighted) only for a self-serve kiosk demo.
 */
class SlotMachineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    // ---- Public API ---------------------------------------------------------

    data class Symbol(val key: String, val color: Int, val prize: String)

    /** Fired on the UI thread when all reels stop. */
    var onResult: ((symbols: IntArray, isWin: Boolean, prize: String?) -> Unit)? = null

    var soundEnabled: Boolean = true
    var respectReducedMotion: Boolean = true

    // ---- CONFIG (modular settings — edit freely) ----------------------------
    /** The reel symbols (label / colour / prize) — fully editable. Keep 6 entries. */
    var symbols: Array<Symbol> = DEFAULT_SYMBOLS
    /** Base spin time of the first reel, ms (before the global [GameStyle.speedFactor]). */
    var reelBaseDurationMs: Float = 1700f
    /** Extra time each later reel keeps spinning, ms (the staggered stop). */
    var reelStaggerMs: Float = 520f
    /** Demo-only odds that [spin] produces a win (0..1). Production uses [spinTo]. */
    var demoWinChance: Float = 0.38f

    val isSpinning: Boolean get() = state == SPINNING

    /** Land [result] (one symbol index per reel) on the payline. */
    fun spinTo(result: IntArray) {
        if (state == SPINNING || result.size < REELS) return
        startSpin(result.copyOf())
    }

    /** Demo helper — random outcome, weighted so wins feel rewarding (~38%). */
    fun spin() {
        val result = IntArray(REELS)
        if (Random.nextFloat() < demoWinChance) {
            val s = Random.nextInt(N); for (i in 0 until REELS) result[i] = s
        } else {
            for (i in 0 until REELS) result[i] = Random.nextInt(N)
            if (result[0] == result[1] && result[1] == result[2]) result[2] = (result[2] + 1) % N
        }
        spinTo(result)
    }

    /** Pull the lever (animates, then spins). What a tap does. */
    fun pullLever() {
        if (state == SPINNING) return
        leverTarget = 1f
        if (soundEnabled) audio.lever()
        postDelayed({ leverTarget = 0f; spin() }, 230)
    }

    // ---- State --------------------------------------------------------------

    private var state = IDLE
    private var U = 0f

    private var cabinetSprite: Bitmap? = null
    private val symbolSprites = arrayOfNulls<Bitmap>(N)

    // cabinet metrics
    private var cabX = 0f; private var cabY = 0f; private var cabW = 0f; private var cabH = 0f
    private var winX = 0f; private var winY = 0f; private var winW = 0f; private var winH = 0f
    private var reelW = 0f; private var reelGap = 0f; private var cellH = 0f; private var paylineY = 0f
    private var leverPivotX = 0f; private var leverPivotY = 0f; private var leverLen = 0f

    // reels
    private class Reel { var offset = Random.nextInt(N).toFloat(); var spinning = false
        var start = 0f; var target = 0f; var t0 = 0L; var dur = 0f; var lastTick = 0 }
    private val reels = Array(REELS) { Reel() }

    private var leverT = 0f; private var leverTarget = 0f; private var leverVel = 0f
    private var frameFlash = 0f
    private var winSym = -1
    private val confetti = ArrayList<Conf>()

    // ---- Paints -------------------------------------------------------------

    private val tmp = Paint(Paint.ANTI_ALIAS_FLAG)
    private val spritePaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bulbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val confPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }

    private val oval = RectF()
    private val path = Path()
    private val audio: SlotAudio by lazy { SlotAudio(context) }

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
        recycleSprites(); audio.release(); super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        U = min(w, h).toFloat()
        layout(w, h)
        buildSprites()
    }

    private fun layout(w: Int, h: Int) {
        cabH = min(h * 0.86f, U * 0.95f)
        cabW = min(w * 0.62f, cabH * 1.25f)
        cabX = (w - cabW) / 2f - U * 0.04f
        cabY = (h - cabH) / 2f + cabH * 0.05f
        val pad = cabW * 0.07f
        winX = cabX + pad; winY = cabY + cabH * 0.30f; winW = cabW - pad * 2; winH = cabH * 0.50f
        reelGap = winW * 0.035f
        reelW = (winW - reelGap * (REELS - 1)) / REELS
        cellH = winH / 3f
        paylineY = winY + winH / 2f
        leverPivotX = cabX + cabW + U * 0.055f
        leverPivotY = cabY + cabH * 0.62f
        leverLen = cabH * 0.30f
    }

    // ---- Spin ---------------------------------------------------------------

    private var pendingResult: IntArray? = null

    private fun startSpin(result: IntArray) {
        state = SPINNING; frameFlash = 0f; winSym = -1
        val reduced = respectReducedMotion && isReducedMotion()
        val now = System.nanoTime()
        for (i in 0 until REELS) {
            val r = reels[i]
            val base = floor(r.offset).toInt()
            val t = result[i]
            val delta = (((t - (base % N)) % N) + N) % N
            val fullSpins = 4 + i
            r.start = r.offset
            r.target = (base + delta + fullSpins * N).toFloat()
            r.t0 = now
            r.dur = ((if (reduced) 700f else reelBaseDurationMs) + i * (if (reduced) 250f else reelStaggerMs)) / GameStyle.speedFactor
            r.spinning = true
            r.lastTick = floor(r.offset).toInt()
        }
        pendingResult = result
        if (soundEnabled) audio.warm()
    }

    private fun step(nowNs: Long) {
        val dt = if (lastFrameNs == 0L) 1f else min((nowNs - lastFrameNs) / 16_666_667f, 3f)
        lastFrameNs = nowNs

        if (state == SPINNING) {
            for (i in 0 until REELS) {
                val r = reels[i]
                if (!r.spinning) continue
                val t = min((nowNs - r.t0) / 1_000_000f / r.dur, 1f)
                r.offset = r.start + (r.target - r.start) * easeOutQuart(t)
                val fl = floor(r.offset).toInt()
                if (fl != r.lastTick) { if (soundEnabled) audio.tick(1f - t); r.lastTick = fl }
                if (t >= 1f) { r.offset = ((r.target % N) + N) % N; r.spinning = false; if (soundEnabled) audio.clunk() }
            }
            if (reels.all { !it.spinning }) onAllStopped()
        }

        // lever spring
        leverVel += ((leverTarget - leverT) * 26f - leverVel * 9f) * (dt / 60f)
        leverT += leverVel * (dt / 60f)

        if (frameFlash > 0f) frameFlash -= dt * 0.04f
        if (confetti.isNotEmpty()) updateConfetti(dt)

        invalidate()
    }

    private fun onAllStopped() {
        state = IDLE
        val syms = IntArray(REELS) { (Math.round(reels[it].offset) % N) }
        val isWin = syms[0] == syms[1] && syms[1] == syms[2]
        val prize = if (isWin) symbols[syms[0]].prize else null
        if (isWin) { winSym = syms[0]; frameFlash = 1f; burstConfetti(); if (soundEnabled) audio.fanfare() }
        onResult?.let { cb -> post { cb(syms, isWin, prize) } }
    }

    // ---- Sprites ------------------------------------------------------------

    private fun buildSprites() {
        recycleSprites()
        buildCabinet()
        for (i in 0 until N) symbolSprites[i] = buildSymbolTile(i)
    }

    private fun buildCabinet() {
        cabinetSprite = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(cabinetSprite!!)
        tmp.alpha = 255

        // shadow
        tmp.shader = RadialGradient(cabX + cabW / 2, cabY + cabH * 0.6f, cabW * 0.7f,
            intArrayOf(Color.argb(115, 0, 0, 0), Color.argb(0, 0, 0, 0)), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        c.drawRect(cabX - cabW * 0.2f, cabY - cabH * 0.05f, cabX + cabW * 1.2f, cabY + cabH * 1.15f, tmp)
        tmp.shader = null

        // body
        roundRect(cabX, cabY, cabW, cabH, cabW * 0.06f)
        tmp.shader = LinearGradient(cabX, cabY, cabX, cabY + cabH,
            intArrayOf(Color.parseColor("#27407E"), Color.parseColor("#1B2A57"), Color.parseColor("#111A38")),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        c.drawPath(path, tmp); tmp.shader = null

        // gold trim
        tmp.style = Paint.Style.STROKE; tmp.strokeWidth = maxOf(2f, cabW * 0.012f)
        tmp.shader = LinearGradient(cabX, cabY, cabX + cabW, cabY + cabH,
            intArrayOf(Color.parseColor("#FFE9A8"), Color.parseColor("#C9962B"), Color.parseColor("#8A651A")),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        roundRect(cabX, cabY, cabW, cabH, cabW * 0.06f); c.drawPath(path, tmp)
        tmp.shader = null; tmp.style = Paint.Style.FILL

        // crown plate
        val crownH = cabH * 0.2f
        roundRect(cabX + cabW * 0.16f, cabY - crownH * 0.45f, cabW * 0.68f, crownH, crownH * 0.4f)
        tmp.shader = LinearGradient(0f, cabY - crownH * 0.45f, 0f, cabY + crownH * 0.55f,
            intArrayOf(Color.parseColor("#FFE9A8"), Color.parseColor("#C9962B")), null, Shader.TileMode.CLAMP)
        c.drawPath(path, tmp); tmp.shader = null
        textPaint.color = Color.parseColor("#2A1D00"); textPaint.textSize = crownH * 0.4f
        c.drawText("★  JACKPOT  ★", cabX + cabW / 2, cabY + crownH * 0.05f - (textPaint.fontMetrics.ascent + textPaint.fontMetrics.descent) / 2, textPaint)

        // reel window inset
        roundRect(winX - winW * 0.02f, winY - winH * 0.04f, winW * 1.04f, winH * 1.08f, winW * 0.03f)
        tmp.color = Color.parseColor("#05070F"); c.drawPath(path, tmp)
        tmp.style = Paint.Style.STROKE; tmp.strokeWidth = maxOf(2f, cabW * 0.01f); tmp.color = Color.parseColor("#0C1330")
        c.drawPath(path, tmp); tmp.style = Paint.Style.FILL

        // separators
        for (i in 1 until REELS) {
            val x = winX + i * reelW + (i - 0.5f) * reelGap
            tmp.color = Color.argb(128, 0, 0, 0)
            c.drawRect(x - reelGap * 0.5f, winY - winH * 0.04f, x + reelGap * 0.5f, winY + winH * 1.04f, tmp)
        }
    }

    private fun buildSymbolTile(i: Int): Bitmap {
        val w = reelW.toInt().coerceAtLeast(2); val h = cellH.toInt().coerceAtLeast(2)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        tmp.alpha = 255   // shared paint — clear any alpha left by a prior tile's icon highlight
        val m = min(reelW, cellH) * 0.07f
        roundRect(m, m, reelW - 2 * m, cellH - 2 * m, min(reelW, cellH) * 0.14f)
        tmp.shader = LinearGradient(0f, m, 0f, cellH - m,
            intArrayOf(Color.WHITE, Color.parseColor("#DFE7FB")), null, Shader.TileMode.CLAMP)
        c.drawPath(path, tmp); tmp.shader = null
        tmp.style = Paint.Style.STROKE; tmp.strokeWidth = 2f; tmp.color = Color.argb(46, 30, 40, 80)
        c.drawPath(path, tmp); tmp.style = Paint.Style.FILL

        val s = min(reelW, cellH) * 0.56f
        c.save(); c.translate(reelW / 2f, cellH / 2f)
        drawIcon(c, symbols[i].key, s, symbols[i].color)
        c.restore()
        return bmp
    }

    private fun drawIcon(c: Canvas, key: String, s: Float, color: Int) {
        tmp.alpha = 255; fillPaint.alpha = 255   // start each icon fully opaque
        when (key) {
            "seven" -> {
                textPaint.textSize = s * 1.15f
                val dy = -(textPaint.fontMetrics.ascent + textPaint.fontMetrics.descent) / 2
                textPaint.color = shade(color, -30); c.drawText("7", 2f, dy + s * 0.05f + 2f, textPaint)
                textPaint.color = color; c.drawText("7", 0f, dy + s * 0.05f, textPaint)
            }
            "bar" -> {
                roundRect(-s * 0.5f, -s * 0.22f, s, s * 0.44f, s * 0.1f)
                tmp.shader = LinearGradient(0f, -s * 0.22f, 0f, s * 0.22f, intArrayOf(shade(color, 30), shade(color, -20)), null, Shader.TileMode.CLAMP)
                c.drawPath(path, tmp); tmp.shader = null
                textPaint.color = Color.WHITE; textPaint.textSize = s * 0.3f
                val dy = -(textPaint.fontMetrics.ascent + textPaint.fontMetrics.descent) / 2
                c.drawText("BAR", 0f, dy + s * 0.01f, textPaint)
            }
            "cherry" -> {
                linePaint.color = Color.parseColor("#3FB6A8"); linePaint.strokeWidth = s * 0.05f
                path.reset(); path.moveTo(-s * 0.18f, s * 0.14f); path.quadTo(s * 0.02f, -s * 0.28f, s * 0.04f, -s * 0.34f); c.drawPath(path, linePaint)
                path.reset(); path.moveTo(s * 0.2f, s * 0.16f); path.quadTo(s * 0.06f, -s * 0.26f, s * 0.04f, -s * 0.34f); c.drawPath(path, linePaint)
                fillPaint.color = Color.parseColor("#3FB6A8")
                oval.set(s * 0.16f - s * 0.12f, -s * 0.32f - s * 0.06f, s * 0.16f + s * 0.12f, -s * 0.32f + s * 0.06f)
                c.save(); c.rotate(-34f, s * 0.16f, -s * 0.32f); c.drawOval(oval, fillPaint); c.restore()
                for (cxy in arrayOf(floatArrayOf(-s * 0.18f, s * 0.26f), floatArrayOf(s * 0.2f, s * 0.28f))) {
                    fillPaint.alpha = 255
                    fillPaint.shader = RadialGradient(cxy[0] - s * 0.05f, cxy[1] - s * 0.05f, s * 0.2f,
                        intArrayOf(shade(color, 40), shade(color, -20)), null, Shader.TileMode.CLAMP)
                    c.drawCircle(cxy[0], cxy[1], s * 0.17f, fillPaint); fillPaint.shader = null
                    fillPaint.color = Color.argb(128, 255, 255, 255); c.drawCircle(cxy[0] - s * 0.06f, cxy[1] - s * 0.06f, s * 0.04f, fillPaint)
                }
            }
            "bell" -> {
                tmp.shader = LinearGradient(0f, -s * 0.4f, 0f, s * 0.3f, intArrayOf(shade(color, 45), shade(color, -15)), null, Shader.TileMode.CLAMP)
                path.reset(); path.moveTo(0f, -s * 0.4f)
                path.cubicTo(s * 0.3f, -s * 0.36f, s * 0.34f, s * 0.1f, s * 0.4f, s * 0.24f)
                path.lineTo(-s * 0.4f, s * 0.24f)
                path.cubicTo(-s * 0.34f, s * 0.1f, -s * 0.3f, -s * 0.36f, 0f, -s * 0.4f)
                c.drawPath(path, tmp); tmp.shader = null
                tmp.color = shade(color, -25); roundRect(-s * 0.44f, s * 0.24f, s * 0.88f, s * 0.08f, s * 0.04f); c.drawPath(path, tmp)
                c.drawCircle(0f, s * 0.38f, s * 0.07f, tmp)
                tmp.color = Color.argb(115, 255, 255, 255); oval.set(-s * 0.17f, -s * 0.26f, -s * 0.07f, s * 0.06f); c.drawOval(oval, tmp)
            }
            "star" -> {
                tmp.shader = LinearGradient(0f, -s * 0.45f, 0f, s * 0.4f, intArrayOf(shade(color, 40), shade(color, -20)), null, Shader.TileMode.CLAMP)
                starPath(0f, 0f, s * 0.48f, s * 0.2f, 5); c.drawPath(path, tmp); tmp.shader = null
                tmp.color = Color.argb(102, 255, 255, 255); starPath(0f, -s * 0.02f, s * 0.3f, s * 0.12f, 5); c.drawPath(path, tmp)
            }
            "diamond" -> {
                tmp.shader = LinearGradient(0f, -s * 0.5f, 0f, s * 0.5f, intArrayOf(shade(color, 40), shade(color, -25)), null, Shader.TileMode.CLAMP)
                path.reset(); path.moveTo(0f, -s * 0.45f); path.lineTo(s * 0.42f, -s * 0.08f); path.lineTo(0f, s * 0.45f); path.lineTo(-s * 0.42f, -s * 0.08f); path.close()
                c.drawPath(path, tmp); tmp.shader = null
                linePaint.color = Color.argb(128, 255, 255, 255); linePaint.strokeWidth = s * 0.025f
                path.reset(); path.moveTo(-s * 0.42f, -s * 0.08f); path.lineTo(s * 0.42f, -s * 0.08f)
                path.moveTo(0f, -s * 0.45f); path.lineTo(-s * 0.18f, -s * 0.08f); path.lineTo(0f, s * 0.45f)
                path.moveTo(0f, -s * 0.45f); path.lineTo(s * 0.18f, -s * 0.08f); c.drawPath(path, linePaint)
                tmp.color = Color.argb(128, 255, 255, 255); path.reset(); path.moveTo(0f, -s * 0.45f); path.lineTo(s * 0.12f, -s * 0.16f); path.lineTo(-s * 0.12f, -s * 0.16f); path.close(); c.drawPath(path, tmp)
            }
        }
    }

    private fun starPath(cx: Float, cy: Float, R: Float, r: Float, n: Int) {
        path.reset()
        for (i in 0 until n * 2) {
            val ang = (i.toFloat() / (n * 2)) * TWO_PI - HALF_PI
            val rad = if (i % 2 == 0) R else r
            val x = cx + cos(ang) * rad; val y = cy + sin(ang) * rad
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
    }

    private fun recycleSprites() {
        cabinetSprite?.recycle(); cabinetSprite = null
        for (i in symbolSprites.indices) { symbolSprites[i]?.recycle(); symbolSprites[i] = null }
    }

    // ---- Draw ---------------------------------------------------------------

    override fun onDraw(canvas: Canvas) {
        if (U <= 0f) return
        cabinetSprite?.let { canvas.drawBitmap(it, 0f, 0f, spritePaint) }
        drawReels(canvas)
        drawPayline(canvas)
        drawLights(canvas)
        drawLever(canvas)
        if (confetti.isNotEmpty()) drawConfetti(canvas)
    }

    private fun drawReels(canvas: Canvas) {
        tmp.alpha = 255   // depth-shading gradients must use full alpha
        val centerY = winY + winH / 2f
        for (i in 0 until REELS) {
            val rx = winX + i * (reelW + reelGap)
            canvas.save()
            canvas.clipRect(rx, winY, rx + reelW, winY + winH)
            val r = reels[i]
            val baseIndex = floor(r.offset).toInt()
            val frac = r.offset - baseIndex
            for (row in -2..2) {
                val idx = (((baseIndex + row) % N) + N) % N
                val y = centerY + row * cellH - frac * cellH
                symbolSprites[idx]?.let { canvas.drawBitmap(it, rx, y - cellH / 2, spritePaint) }
            }
            canvas.restore()
            // depth shading
            tmp.shader = LinearGradient(0f, winY, 0f, winY + cellH * 0.9f,
                intArrayOf(Color.argb(235, 5, 7, 15), Color.argb(0, 5, 7, 15)), null, Shader.TileMode.CLAMP)
            canvas.drawRect(rx, winY, rx + reelW, winY + cellH * 0.9f, tmp)
            tmp.shader = LinearGradient(0f, winY + winH - cellH * 0.9f, 0f, winY + winH,
                intArrayOf(Color.argb(0, 5, 7, 15), Color.argb(235, 5, 7, 15)), null, Shader.TileMode.CLAMP)
            canvas.drawRect(rx, winY + winH - cellH * 0.9f, rx + reelW, winY + winH, tmp)
            tmp.shader = null
        }
    }

    private fun drawPayline(canvas: Canvas) {
        val flashing = winSym >= 0 && frameFlash > 0f
        linePaint.color = if (flashing)
            Color.argb((128 + 127 * kotlin.math.abs(sin(System.nanoTime() / 120_000_000f))).toInt().coerceIn(0, 255), 255, 212, 94)
        else Color.argb(140, 255, 212, 94)
        linePaint.strokeWidth = maxOf(2f, U * 0.006f)
        canvas.drawLine(winX - U * 0.01f, paylineY, winX + winW + U * 0.01f, paylineY, linePaint)
        val a = U * 0.018f
        fillPaint.color = Color.parseColor("#FFD45E")
        for (side in floatArrayOf(winX - U * 0.012f, winX + winW + U * 0.012f)) {
            val dir = if (side < winX) 1f else -1f
            path.reset(); path.moveTo(side, paylineY); path.lineTo(side - dir * a, paylineY - a * 0.6f); path.lineTo(side - dir * a, paylineY + a * 0.6f); path.close()
            canvas.drawPath(path, fillPaint)
        }
    }

    private fun drawLights(canvas: Canvas) {
        val n = 18
        val now = System.nanoTime() / 1_000_000f
        val r = maxOf(3f, cabW * 0.012f)
        for (i in 0 until n) {
            val f = i / (n - 1f)
            val x = cabX + cabW * 0.1f + f * cabW * 0.8f
            val y = cabY + cabH * 0.02f + sin(f * PI.toFloat()) * (-cabH * 0.02f)
            val lit = when {
                state == SPINNING -> ((now / 80f).toInt() + i) % 2 == 0
                winSym >= 0 && frameFlash > 0f -> ((now / 100f).toInt() + i) % 2 == 0
                else -> sin(now / 600f - i * 0.5f) > 0.3f
            }
            bulbPaint.color = if (lit) Color.parseColor("#FFF6D8") else Color.parseColor("#5B4A1E")
            if (lit) bulbPaint.setShadowLayer(12f, 0f, 0f, Color.parseColor("#FFE79A")) else bulbPaint.clearShadowLayer()
            canvas.drawCircle(x, y, r, bulbPaint)
        }
        bulbPaint.clearShadowLayer()
    }

    private fun drawLever(canvas: Canvas) {
        fillPaint.color = Color.parseColor("#0C1330")
        canvas.drawCircle(leverPivotX, leverPivotY, leverLen * 0.18f, fillPaint)
        linePaint.color = Color.parseColor("#C9962B"); linePaint.strokeWidth = maxOf(2f, U * 0.004f)
        canvas.drawCircle(leverPivotX, leverPivotY, leverLen * 0.18f, linePaint)

        val restAng = -PI.toFloat() * 0.5f
        val pullAng = -PI.toFloat() * 0.16f
        val ang = restAng + (pullAng - restAng) * leverT
        val kx = leverPivotX + cos(ang) * leverLen
        val ky = leverPivotY + sin(ang) * leverLen
        linePaint.strokeWidth = leverLen * 0.12f
        linePaint.shader = LinearGradient(leverPivotX, leverPivotY, kx, ky,
            intArrayOf(Color.parseColor("#9AA3C7"), Color.parseColor("#E8EEFF")), null, Shader.TileMode.CLAMP)
        canvas.drawLine(leverPivotX, leverPivotY, kx, ky, linePaint); linePaint.shader = null

        val kr = leverLen * 0.2f
        fillPaint.shader = RadialGradient(kx - kr * 0.35f, ky - kr * 0.4f, kr,
            intArrayOf(Color.parseColor("#FF8A93"), Color.parseColor("#E94B5A"), Color.parseColor("#A32D39")),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        canvas.drawCircle(kx, ky, kr, fillPaint); fillPaint.shader = null
        fillPaint.color = Color.argb(128, 255, 255, 255); canvas.drawCircle(kx - kr * 0.32f, ky - kr * 0.36f, kr * 0.22f, fillPaint)
        knobX = kx; knobY = ky; knobR = kr * 1.6f
    }

    // ---- Confetti -----------------------------------------------------------

    private class Conf(var x: Float, var y: Float, var vx: Float, var vy: Float, var rot: Float, val vr: Float,
                       val w: Float, val h: Float, val color: Int, var wobble: Float)
    private fun burstConfetti() {
        if (respectReducedMotion && isReducedMotion()) return
        for (s in 0 until 2) {
            val x = if (s == 0) width * 0.05f else width * 0.95f; val y = height * 0.9f
            repeat(70) {
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

    private var knobX = 0f; private var knobY = 0f; private var knobR = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && state != SPINNING) {
            performClick(); pullLever(); return true
        }
        return super.onTouchEvent(event)
    }
    override fun performClick(): Boolean { super.performClick(); return true }

    // ---- Helpers ------------------------------------------------------------

    private fun roundRect(x: Float, y: Float, w: Float, h: Float, r: Float) {
        path.reset(); oval.set(x, y, x + w, y + h); path.addRoundRect(oval, r, r, Path.Direction.CW)
    }
    private fun isReducedMotion(): Boolean = try {
        android.provider.Settings.Global.getFloat(context.contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    } catch (e: Exception) { false }

    companion object {
        private const val IDLE = 0
        private const val SPINNING = 1
        private const val REELS = 3
        private const val TWO_PI = (PI * 2).toFloat()
        private const val HALF_PI = (PI / 2).toFloat()
        private val easeOutQuart = { t: Float -> 1f - (1f - t).pow(4) }

        val DEFAULT_SYMBOLS = arrayOf(
            Symbol("seven", Color.parseColor("#E94B5A"), "JACKPOT — Free Drink"),
            Symbol("star", Color.parseColor("#FFD45E"), "Free Cookie"),
            Symbol("bar", Color.parseColor("#3FA7E0"), "10% Off Today"),
            Symbol("cherry", Color.parseColor("#E94B5A"), "Free Topping"),
            Symbol("bell", Color.parseColor("#F2A03D"), "Free Upgrade"),
            Symbol("diamond", Color.parseColor("#22D3EE"), "20% Off Today"),
        )
        private const val N = 6
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
