package com.posdemo.spinwheel

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Demo host for [ShellGameView].
 *
 * Production usage mirrors the wheel/slot: the back office decides the outcome
 * and the reveal animates to it.
 *
 *     shell.onResult = { won, prize -> if (won) pos.recordReward(prize) }
 *     shell.newRound(shouldWin = rewardEngine.decide(customer), prize = "Free Coffee")
 *
 * This screen auto-plays rounds with a random outcome and shows the result —
 * for review. Tap a cup to pick.
 */
class ShellActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shell)

        val shell = findViewById<ShellGameView>(R.id.shellGame)
        val result = findViewById<TextView>(R.id.shellResult)
        val playBtn = findViewById<Button>(R.id.shellPlayButton)

        shell.onResult = { won, prize ->
            result.text = if (won) "🎉 $prize" else "Not this time — play again!"
        }

        playBtn.setOnClickListener {
            result.text = ""
            shell.newRound()
        }

        // Auto-play one round on launch so the display is "alive".
        shell.post { shell.newRound() }
    }
}
