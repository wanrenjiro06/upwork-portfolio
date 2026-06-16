package com.fox.random

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView

/**
 * Drives the self-contained "Thank You" celebration shown in the RateUs dialog.
 *
 * Recreates the look of an animated GIF using only views + property animation
 * (no binary asset, so it works offline and adds no app weight):
 *   - stars pop in one by one with an overshoot bounce and a little spin,
 *   - the "THANK YOU!" text pulses and cycles through bright colors.
 *
 * To use a real GIF instead, drop it in res/raw and load it into an ImageView
 * with Glide (`Glide.with(view).asGif().load(R.raw.thank_you).into(iv)`).
 */
object ThankYouAnimator {

    private val COLORS = intArrayOf(
        0xFFE91E63.toInt(), // magenta
        0xFF7C4DFF.toInt(), // violet
        0xFF00BCD4.toInt(), // cyan
        0xFFFF7043.toInt(), // orange
        0xFFE91E63.toInt()  // back to magenta (smooth loop)
    )

    fun play(stars: List<View>, thankYou: TextView) {
        // Stars: start hidden, then pop in with a staggered bounce.
        stars.forEachIndexed { index, star ->
            star.scaleX = 0f
            star.scaleY = 0f
            star.rotation = -45f
            star.animate()
                .scaleX(1f).scaleY(1f)
                .rotation(0f)
                .setStartDelay(120L + index * 90L)
                .setDuration(420L)
                .setInterpolator(OvershootInterpolator(3f))
                .start()
        }

        // Text: gentle pulse that repeats forever.
        thankYou.scaleX = 0.7f
        thankYou.scaleY = 0.7f
        thankYou.animate()
            .scaleX(1f).scaleY(1f)
            .setStartDelay(150L)
            .setDuration(500L)
            .setInterpolator(OvershootInterpolator(4f))
            .withEndAction { startPulse(thankYou) }
            .start()

        // Text: cycle through bright colors like a celebratory GIF.
        ValueAnimator.ofObject(ArgbEvaluator(), *COLORS.toTypedArray()).apply {
            duration = 2600L
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { thankYou.setTextColor(it.animatedValue as Int) }
            start()
        }
    }

    private fun startPulse(view: TextView) {
        ValueAnimator.ofFloat(1f, 1.08f, 1f).apply {
            duration = 1200L
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                val s = it.animatedValue as Float
                view.scaleX = s
                view.scaleY = s
            }
            start()
        }
    }
}
