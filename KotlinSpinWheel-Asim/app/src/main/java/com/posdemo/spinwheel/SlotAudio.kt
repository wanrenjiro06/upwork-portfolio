package com.posdemo.spinwheel

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

/**
 * SlotAudio
 * ----------------------------------------------------------------------------
 * Procedurally-synthesized slot SFX — NO bundled audio assets, mirroring the
 * web demo's Web Audio approach and the WheelAudio / BirthdayAudio pattern.
 *
 *   tick(speed) — a reel click as each symbol passes the payline (pitch rises
 *                 with reel speed via SoundPool playback rate).
 *   clunk()     — a reel locking into place.
 *   lever()     — the lever-pull sound.
 *   fanfare()   — the win jingle.
 *
 * Clips are synthesized once at init, written to the app cache as WAV, and
 * played through a low-latency SoundPool (overlapping, ideal for rapid ticks).
 */
class SlotAudio(context: Context) {

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
    private var clunkId = 0
    private var leverId = 0
    private var fanfareId = 0
    private var loaded = false

    init {
        try {
            val cache = context.cacheDir
            val tickWav = File(cache, "sl_tick.wav")
            val clunkWav = File(cache, "sl_clunk.wav")
            val leverWav = File(cache, "sl_lever.wav")
            val fanWav = File(cache, "sl_fanfare.wav")
            writeWav(tickWav, synthTick())
            writeWav(clunkWav, synthClunk())
            writeWav(leverWav, synthLever())
            writeWav(fanWav, synthFanfare())
            tickId = pool.load(tickWav.absolutePath, 1)
            clunkId = pool.load(clunkWav.absolutePath, 1)
            leverId = pool.load(leverWav.absolutePath, 1)
            fanfareId = pool.load(fanWav.absolutePath, 1)
            pool.setOnLoadCompleteListener { _, _, status -> if (status == 0) loaded = true }
        } catch (e: Exception) {
            // Audio is non-essential; a silent kiosk still works.
        }
    }

    fun warm() { /* SoundPool needs no resume; kept for API parity with the web demo. */ }

    /** Reel click. [speed] in 0..1 (1 = fast) raises the pitch. */
    fun tick(speed: Float) {
        if (!loaded) return
        val rate = 1.0f + speed.coerceIn(0f, 1f) * 0.7f
        pool.play(tickId, 0.4f, 0.4f, 1, 0, rate)
    }

    fun clunk()   { if (loaded) pool.play(clunkId, 0.7f, 0.7f, 1, 0, 1f) }
    fun lever()   { if (loaded) pool.play(leverId, 0.6f, 0.6f, 1, 0, 1f) }
    fun fanfare() { if (loaded) pool.play(fanfareId, 0.85f, 0.85f, 1, 0, 1f) }

    fun release() { try { pool.release() } catch (e: Exception) { } }

    // ---- Synthesis ----------------------------------------------------------

    /** Short square blip with fast decay (~45ms). */
    private fun synthTick(): ShortArray {
        val dur = 0.045
        val n = (SAMPLE_RATE * dur).toInt()
        val out = ShortArray(n)
        val freq = 460.0
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 70.0)
            val s = if ((t * freq) % 1.0 < 0.5) 1.0 else -1.0      // square
            out[i] = (s * env * 0.4 * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    /** Low thump for a reel locking in. */
    private fun synthClunk(): ShortArray {
        val dur = 0.18
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 24.0)
            out[i] = (sin(2 * PI * 150.0 * t) * 0.7 + sin(2 * PI * 90.0 * t) * 0.5) * env * 0.5
        }
        return toShorts(out)
    }

    /** Two-tone descending sawtooth for the lever pull. */
    private fun synthLever(): ShortArray {
        val dur = 0.22
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val f = 300.0 - 140.0 * (t / dur)                     // sweep down
            val saw = 2.0 * ((t * f) % 1.0) - 1.0
            val env = exp(-t * 9.0)
            out[i] = saw * env * 0.35
        }
        return toShorts(out)
    }

    /** Five ascending triangle notes — the win jingle. */
    private fun synthFanfare(): ShortArray {
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5, 1318.5)
        val gap = 0.1
        val total = (notes.size - 1) * gap + 0.5 + 0.1
        val n = (SAMPLE_RATE * total).toInt()
        val out = DoubleArray(n)
        for ((k, f) in notes.withIndex()) {
            val start = (SAMPLE_RATE * k * gap).toInt()
            val len = (SAMPLE_RATE * 0.5).toInt()
            for (i in 0 until len) {
                val idx = start + i
                if (idx >= n) break
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-t * 6.0)
                out[idx] += triangle(f, t) * env * 0.32
            }
        }
        return toShorts(out)
    }

    private fun triangle(freq: Double, t: Double): Double {
        val p = (t * freq) % 1.0
        return 4.0 * abs(p - 0.5) - 1.0
    }

    private fun toShorts(buf: DoubleArray): ShortArray {
        val res = ShortArray(buf.size)
        for (i in buf.indices) {
            val v = buf[i].coerceIn(-1.0, 1.0)
            res[i] = (v * Short.MAX_VALUE).toInt().toShort()
        }
        return res
    }

    private fun writeWav(file: File, pcm: ShortArray) {
        val byteRate = SAMPLE_RATE * 2
        val dataSize = pcm.size * 2
        FileOutputStream(file).use { fos ->
            fos.write("RIFF".toByteArray()); fos.write(intLE(36 + dataSize)); fos.write("WAVE".toByteArray())
            fos.write("fmt ".toByteArray()); fos.write(intLE(16)); fos.write(shortLE(1)); fos.write(shortLE(1))
            fos.write(intLE(SAMPLE_RATE)); fos.write(intLE(byteRate)); fos.write(shortLE(2)); fos.write(shortLE(16))
            fos.write("data".toByteArray()); fos.write(intLE(dataSize))
            val bytes = ByteArray(dataSize)
            for (i in pcm.indices) {
                bytes[i * 2] = (pcm[i].toInt() and 0xff).toByte()
                bytes[i * 2 + 1] = ((pcm[i].toInt() shr 8) and 0xff).toByte()
            }
            fos.write(bytes)
        }
    }

    private fun intLE(v: Int) = byteArrayOf(
        (v and 0xff).toByte(), ((v shr 8) and 0xff).toByte(), ((v shr 16) and 0xff).toByte(), ((v shr 24) and 0xff).toByte()
    )
    private fun shortLE(v: Int) = byteArrayOf((v and 0xff).toByte(), ((v shr 8) and 0xff).toByte())

    companion object { private const val SAMPLE_RATE = 44_100 }
}
