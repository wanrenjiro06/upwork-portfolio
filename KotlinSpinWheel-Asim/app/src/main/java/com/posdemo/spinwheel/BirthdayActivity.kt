package com.posdemo.spinwheel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

/**
 * Demo host for [BirthdayCelebrationView].
 *
 * Production usage is a single call — the POS knows it's the customer's
 * birthday (loyalty profile) and just triggers the show:
 *
 *     birthdayView.onComplete = { /* return to normal POS UI */ }
 *     birthdayView.celebrate(customerName)
 *
 * This screen adds a name field + button purely so the celebration can be
 * replayed with any name during review. Tapping the cake blows the candles out.
 */
class BirthdayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birthday)

        val view = findViewById<BirthdayCelebrationView>(R.id.birthdayView)
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val celebrate = findViewById<Button>(R.id.celebrateButton)

        celebrate.setOnClickListener {
            view.celebrate(nameInput.text?.toString().orEmpty())
        }

        // Auto-play once so the display is "alive" on launch.
        view.post { view.celebrate(nameInput.text?.toString().orEmpty()) }
    }
}
