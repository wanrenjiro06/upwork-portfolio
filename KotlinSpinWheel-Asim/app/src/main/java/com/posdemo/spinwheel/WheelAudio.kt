package com.posdemo.spinwheel

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * WheelAudio
 * ----------------------------------------------------------------------------
 * Procedurally-synthesized sound — NO bundled audio assets.
 *
 * The web demo generates ticks and a win fanfare on the fly with the Web Audio
 * API. SoundPool can't play raw buffers, so at init we synthesize short PCM
 * clips, write them to the app cache as WAV once, and load them into a
 * SoundPool. Ticks then replay with a pitch (rate) that rises as the wheel
 * slows — exactly like the original.
 *
 * Why SoundPool: ticks fire rapidly and must be low-latency and overlapping;
 * SoundPool is built for exactly that.
 */
class WheelAudio(context: Context) {

    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(8)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var tickId = 0
    private var fanfareId = 0
    private var loaded = false

    init {
        try {
            val cache = context.cacheDir
            val tickWav = File(cache, "sw_tick.wav")
            val fanWav = File(cache, "sw_fanfare.wav")
            writeWav(tickWav, synthTick())
            writeWav(fanWav, synthFanfare())
            tickId = pool.load(tickWav.absolutePath, 1)
            fanfareId = pool.load(fanWav.absolutePath, 1)
            pool.setOnLoadCompleteListener { _, _, status -> if (status == 0) loaded = true }
        } catch (e: Exception) {
            // Audio is non-essential; a kiosk with no audio output still works.
        }
    }

    /** Nudge the audio system; call when a spin begins. */
    fun warm() { /* SoundPool needs no resume; kept for API parity with web. */ }

    /** Play a peg-tick. [speed] in 0..1 (1 = fast) raises the pitch. */
    fun tick(speed: Float) {
        if (!loaded) return
        val rate = (1.0f + speed.coerceIn(0f, 1f) * 0.6f)   // 1.0 .. 1.6
        pool.play(tickId, 0.5f, 0.5f, 1, 0, rate)
    }

    /** Play the win fanfare. */
    fun fanfare() {
        if (!loaded) return
        pool.play(fanfareId, 0.8f, 0.8f, 1, 0, 1f)
    }

    fun release() {
        try { pool.release() } catch (e: Exception) { }
    }

    // ---- Synthesis ----------------------------------------------------------

    /** Short square-ish blip with a fast exponential decay (~70ms). */
    private fun synthTick(): ShortArray {
        val dur = 0.07
        val n = (SAMPLE_RATE * dur).toInt()
        val out = ShortArray(n)
        val freq = 880.0
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 55.0)                       // quick decay
            // square-ish tone = sum of odd harmonics, kept cheap
            val s = sin(2 * PI * freq * t) +
                    0.33 * sin(2 * PI * freq * 3 * t)
            out[i] = (s / 1.33 * env * 0.5 * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    /** Four ascending triangle notes (C-E-G-C) ~0.6s total. */
    private fun synthFanfare(): ShortArray {
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5)
        val noteDur = 0.16
        val gap = 0.10
        val total = ((notes.size - 1) * gap + noteDur + 0.4)
        val n = (SAMPLE_RATE * total).toInt()
        val out = DoubleArray(n)
        for ((k, f) in notes.withIndex()) {
            val start = (SAMPLE_RATE * k * gap).toInt()
            val len = (SAMPLE_RATE * (noteDur + 0.3)).toInt()
            for (i in 0 until len) {
                val idx = start + i
                if (idx >= n) break
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-t * 6.0)
                out[idx] += triangle(f, t) * env * 0.4
            }
        }
        val res = ShortArray(n)
        for (i in 0 until n) {
            val v = out[i].coerceIn(-1.0, 1.0)
            res[i] = (v * Short.MAX_VALUE).toInt().toShort()
        }
        return res
    }

    private fun triangle(freq: Double, t: Double): Double {
        val p = (t * freq) % 1.0
        return 4.0 * kotlin.math.abs(p - 0.5) - 1.0
    }

    /** Write 16-bit mono PCM as a minimal WAV file. */
    private fun writeWav(file: File, pcm: ShortArray) {
        val byteRate = SAMPLE_RATE * 2
        val dataSize = pcm.size * 2
        FileOutputStream(file).use { fos ->
            fos.write("RIFF".toByteArray())
            fos.write(intLE(36 + dataSize))
            fos.write("WAVE".toByteArray())
            fos.write("fmt ".toByteArray())
            fos.write(intLE(16))               // subchunk1 size
            fos.write(shortLE(1))              // PCM
            fos.write(shortLE(1))              // mono
            fos.write(intLE(SAMPLE_RATE))
            fos.write(intLE(byteRate))
            fos.write(shortLE(2))              // block align
            fos.write(shortLE(16))             // bits per sample
            fos.write("data".toByteArray())
            fos.write(intLE(dataSize))
            val bytes = ByteArray(dataSize)
            for (i in pcm.indices) {
                bytes[i * 2] = (pcm[i].toInt() and 0xff).toByte()
                bytes[i * 2 + 1] = ((pcm[i].toInt() shr 8) and 0xff).toByte()
            }
            fos.write(bytes)
        }
    }

    private fun intLE(v: Int) = byteArrayOf(
        (v and 0xff).toByte(),
        ((v shr 8) and 0xff).toByte(),
        ((v shr 16) and 0xff).toByte(),
        ((v shr 24) and 0xff).toByte()
    )

    private fun shortLE(v: Int) = byteArrayOf(
        (v and 0xff).toByte(),
        ((v shr 8) and 0xff).toByte()
    )

    companion object {
        private const val SAMPLE_RATE = 44_100
    }
}
