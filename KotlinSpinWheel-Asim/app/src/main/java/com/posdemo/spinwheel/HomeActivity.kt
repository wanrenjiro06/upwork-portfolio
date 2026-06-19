package com.posdemo.spinwheel

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * Home / menu screen — the single launcher for the POS mini-games.
 *
 * Each tile opens one of the four customer-display games. In a real POS shell
 * the host app would launch the relevant game directly (e.g. fire the birthday
 * celebration when a loyalty profile says it's the customer's birthday); this
 * menu is the convenient all-in-one entry for review and for staff.
 */
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        open(R.id.tileSpin, MainActivity::class.java)
        open(R.id.tileBirthday, BirthdayActivity::class.java)
        open(R.id.tileSlot, SlotActivity::class.java)
        open(R.id.tileShell, ShellActivity::class.java)
    }

    private fun open(viewId: Int, activity: Class<*>) {
        findViewById<View>(viewId).setOnClickListener {
            startActivity(Intent(this, activity))
        }
    }
}
