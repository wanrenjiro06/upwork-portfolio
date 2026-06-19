package com.posdemo.spinwheel

import android.graphics.Color

/**
 * GameStyle — central, edit-in-one-place styling and tuning for all four POS
 * games. Change these values to restyle or re-time every game without touching
 * any rendering code.
 *
 *   • Brand palette: the shared colours. Each game's default colours come from
 *     here, so changing (say) [gold] re-tints the wheel bezel, the slot trim,
 *     the candle flame accents and the cup rims at once.
 *   • [speedFactor]: one global speed knob applied to every animation —
 *     1.0 = normal, 2.0 = twice as fast, 0.5 = half speed.
 *
 * Each game ALSO exposes its own options (segments, symbols, candle count,
 * win odds, per-game durations and colours) as public properties on its View
 * — see the "CONFIG" block at the top of each *View class. Those override the
 * shared defaults for that one game.
 */
object GameStyle {

    // ---- Brand palette (the shared look) ----
    var blue      = Color.parseColor("#3FA7E0")
    var blueLite  = Color.parseColor("#82D2FF")
    var blueDeep  = Color.parseColor("#1E63B0")
    var indigo    = Color.parseColor("#5A6BD8")
    var gold      = Color.parseColor("#FFD45E")
    var goldLite  = Color.parseColor("#FFE9A8")
    var goldDeep  = Color.parseColor("#C9962B")
    var ink       = Color.parseColor("#060912")
    var white     = Color.WHITE
    var text      = Color.parseColor("#F4F6FF")
    var textDim   = Color.parseColor("#9AA8CF")

    /**
     * Global animation speed multiplier applied to every game.
     * 1.0 = normal, > 1 faster, < 1 slower. The games divide their durations
     * by this, so a value of 2.0 makes everything finish in half the time.
     */
    var speedFactor = 1.0f

    /** Helper: scale a duration (ms or seconds) by the global speed factor. */
    fun scaled(duration: Float): Float = duration / speedFactor
}
