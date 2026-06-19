package com.posdemo.spinwheel

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Demo host for [SlotMachineView].
 *
 * Production usage mirrors the wheel: the back office decides the outcome and
 * the machine animates to it.
 *
 *     slot.onResult = { _, isWin, prize -> if (isWin) pos.recordReward(prize) }
 *     slot.spinTo(rewardEngine.decideReels(customer))   // server-chosen symbols
 *
 * This screen wires tap-to-pull-the-lever plus a Spin button, and shows the
 * prize on a win — purely for review.
 */
class SlotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slot)

        val slot = findViewById<SlotMachineView>(R.id.slotMachine)
        val result = findViewById<TextView>(R.id.slotResult)
        val spinBtn = findViewById<Button>(R.id.slotSpinButton)

        slot.onResult = { _, isWin, prize ->
            result.text = if (isWin) "🎉 $prize" else "So close — spin again!"
        }

        spinBtn.setOnClickListener {
            result.text = ""
            slot.pullLever()
        }
    }
}
