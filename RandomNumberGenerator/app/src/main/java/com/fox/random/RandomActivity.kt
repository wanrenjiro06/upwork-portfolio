package com.fox.random

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fox.random.databinding.ActivityRandomBinding
import kotlin.random.Random

/**
 * Activity 2 — Random Number Generator.
 *
 * Generates, each round:
 *   - one 6-digit number   (000000–999999)
 *   - four 3-digit numbers  (000–999)
 *   - one 2-digit number   (00–99)
 *
 * "Random Again" regenerates everything; the share button shares the app.
 */
class RandomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRandomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        generate()

        binding.btnRandomAgain.setOnClickListener { generate() }
        binding.btnShare.setOnClickListener { shareNumbers() }
    }

    /** Fills every field with a fresh zero-padded random number and animates it. */
    private fun generate() {
        setNumber(binding.tv6, Random.nextInt(0, 1_000_000), 6)
        setNumber(binding.tv3a, Random.nextInt(0, 1_000), 3)
        setNumber(binding.tv3b, Random.nextInt(0, 1_000), 3)
        setNumber(binding.tv3c, Random.nextInt(0, 1_000), 3)
        setNumber(binding.tv3d, Random.nextInt(0, 1_000), 3)
        setNumber(binding.tv2, Random.nextInt(0, 100), 2)
    }

    /** Sets a zero-padded value and plays a quick pop animation on the card. */
    private fun setNumber(view: TextView, value: Int, digits: Int) {
        view.text = value.toString().padStart(digits, '0')
        view.scaleX = 0.85f
        view.scaleY = 0.85f
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(220L)
            .start()
    }

    private fun shareNumbers() {
        val text = """
            Random Numbers:
            6 Digits: ${binding.tv6.text}
            3 Digits: ${binding.tv3a.text}  ${binding.tv3b.text}  ${binding.tv3c.text}  ${binding.tv3d.text}
            2 Digits: ${binding.tv2.text}
        """.trimIndent()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }
}
