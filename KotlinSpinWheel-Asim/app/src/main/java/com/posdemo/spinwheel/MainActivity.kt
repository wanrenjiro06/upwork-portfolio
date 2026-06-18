package com.posdemo.spinwheel

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Demo host for [SpinWheelView].
 *
 * Shows the two ways to drive the wheel:
 *   1. Tap-to-spin (built into the view) — good for a self-serve kiosk.
 *   2. [SpinWheelView.spinTo] — the production path, where YOUR POS logic
 *      decides the prize (odds, stock, per-customer rules) and the wheel
 *      just animates to land on it.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wheel = findViewById<SpinWheelView>(R.id.spinWheel)
        val resultLabel = findViewById<TextView>(R.id.resultLabel)
        val spinButton = findViewById<View>(R.id.spinButton)

        // Called when the wheel comes to rest — hook your redemption logic here.
        wheel.onResult = { index, label ->
            resultLabel.text = "🎉 $label"
            // e.g. posController.recordReward(index, label) / print coupon
        }

        // Example of the production path: the SERVER picks the prize, the wheel
        // is told where to stop. Here we just demo with a fixed JACKPOT (index 4).
        spinButton.setOnClickListener {
            resultLabel.text = ""
            val serverChosenPrize = 4   // <- would come from your back office
            wheel.spinTo(serverChosenPrize)
        }
    }
}
