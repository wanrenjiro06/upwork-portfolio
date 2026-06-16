package com.fox.random

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * RateUs logic (per spec):
 *
 *  - 1st launch  : nothing (don't annoy the user).
 *  - 2nd launch onward: show the RateUs dialog.
 *  - Tap CLOSE (X): dismiss, and show again on the next launch.
 *  - Tap RATE     : dismiss, open the in-app review flow, and never show again.
 *
 * State is stored in SharedPreferences so it survives app restarts.
 */
object RateManager {

    private const val PREFS = "rate_prefs"
    private const val KEY_LAUNCH_COUNT = "launch_count"
    private const val KEY_RATED = "rated"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Call once per app launch to increment the launch counter. */
    fun onAppLaunched(context: Context) {
        val p = prefs(context)
        val count = p.getInt(KEY_LAUNCH_COUNT, 0) + 1
        p.edit().putInt(KEY_LAUNCH_COUNT, count).apply()
    }

    /** True when the dialog should be shown this launch (2nd+ launch and not yet rated). */
    fun shouldShow(context: Context): Boolean {
        val p = prefs(context)
        val count = p.getInt(KEY_LAUNCH_COUNT, 0)
        val rated = p.getBoolean(KEY_RATED, false)
        return count >= 2 && !rated
    }

    private fun markRated(context: Context) {
        prefs(context).edit().putBoolean(KEY_RATED, true).apply()
    }

    /** Builds and shows the custom RateUs dialog. */
    fun showRateDialog(activity: Activity) {
        if (activity.isFinishing) return

        val dialog = Dialog(activity, R.style.RateDialogTheme).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_rate_us)
            setCancelable(true)
        }

        // X -> dismiss only; "rated" stays false so it shows again next launch.
        dialog.findViewById<ImageButton>(R.id.ivClose).setOnClickListener {
            dialog.dismiss()
        }

        // Rate -> in-app review, never show again.
        dialog.findViewById<TextView>(R.id.btnRate).setOnClickListener {
            markRated(activity)
            dialog.dismiss()
            launchReview(activity)
        }

        // Play the celebratory "Thank You" animation when the dialog appears.
        dialog.setOnShowListener {
            val stars = listOf(
                dialog.findViewById<ImageView>(R.id.star1),
                dialog.findViewById<ImageView>(R.id.star2),
                dialog.findViewById<ImageView>(R.id.star3),
                dialog.findViewById<ImageView>(R.id.star4),
                dialog.findViewById<ImageView>(R.id.star5)
            )
            val thankYou = dialog.findViewById<TextView>(R.id.tvThankYou)
            ThankYouAnimator.play(stars, thankYou)
        }

        dialog.show()
    }

    /**
     * Launches the Google Play in-app review flow. The dialog/quota is controlled
     * by Play, so we fall back to opening the store listing if it can't be shown.
     */
    private fun launchReview(activity: Activity) {
        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow().addOnCompleteListener { request ->
            if (request.isSuccessful) {
                manager.launchReviewFlow(activity, request.result)
                    .addOnCompleteListener { /* flow finished (result is intentionally opaque) */ }
            } else {
                openPlayStore(activity)
            }
        }
    }

    /** Opens the app's Play Store listing (used as a fallback for the review flow). */
    private fun openPlayStore(activity: Activity) {
        val pkg = activity.packageName
        try {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg"))
            )
        } catch (e: ActivityNotFoundException) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
                )
            )
        }
    }
}
