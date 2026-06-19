package com.posdemo.spinwheel

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.random.Random

/**
 * BirthdayAudio
 * ----------------------------------------------------------------------------
 * Procedurally-synthesized music & SFX — NO bundled audio assets, mirroring the
 * web demo's Web Audio approach and the WheelAudio pattern.
 *
 *   tune()  — the public-domain "Happy Birthday" melody (first two phrases),
 *             rendered to one PCM clip.
 *   poof()  — a short breath of band-passed noise (candles blown out).
 *   tada()  — a 4-note ascending chord (finale).
 *
 * Clips are synthesized once at init, written to the app cache as WAV, and
 * played through a low-latency SoundPool.
 */
class BirthdayAudio(context: Context) {

    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .build()

    private var tuneId = 0
    private var poofId = 0
    private var tadaId = 0
    private var loaded = false

    init {
        try {
            val cache = context.cacheDir
            val tuneWav = File(cache, "bd_tune.wav")
            val poofWav = File(cache, "bd_poof.wav")
            val tadaWav = File(cache, "bd_tada.wav")
            writeWav(tuneWav, synthTune())
            writeWav(poofWav, synthPoof())
            writeWav(tadaWav, synthTada())
            tuneId = pool.load(tuneWav.absolutePath, 1)
            poofId = pool.load(poofWav.absolutePath, 1)
            tadaId = pool.load(tadaWav.absolutePath, 1)
            pool.setOnLoadCompleteListener { _, _, status -> if (status == 0) loaded = true }
        } catch (e: Exception) {
            // Audio is non-essential; a silent kiosk still works.
        }
    }

    fun tune() { if (loaded) pool.play(tuneId, 0.8f, 0.8f, 1, 0, 1f) }
    fun poof() { if (loaded) pool.play(poofId, 0.7f, 0.7f, 1, 0, 1f) }
    fun tada() { if (loaded) pool.play(tadaId, 0.8f, 0.8f, 1, 0, 1f) }

    fun release() { try { pool.release() } catch (e: Exception) { } }

    // ---- Synthesis ----------------------------------------------------------

    /** "Happy Birthday" melody, first two phrases, as soft triangle notes. */
    private fun synthTune(): ShortArray {
        val g4 = 392.00; val a4 = 440.00; val b4 = 493.88; val c5 = 523.25; val d5 = 587.33
        val beat = 0.34
        // (freq, beats)
        val seq = arrayOf(
            g4 to 0.75, g4 to 0.25, a4 to 1.0, g4 to 1.0, c5 to 1.0, b4 to 2.0,
            g4 to 0.75, g4 to 0.25, a4 to 1.0, g4 to 1.0, d5 to 1.0, c5 to 2.0
        )
        val total = seq.sumOf { it.second } * beat + 0.5
        val n = (SAMPLE_RATE * total).toInt()
        val mix = DoubleArray(n)
        var t0 = 0.05
        for ((f, b) in seq) {
            val noteLen = b * beat * 0.92
            val start = (SAMPLE_RATE * t0).toInt()
            val len = (SAMPLE_RATE * (noteLen + 0.18)).toInt()
            for (i in 0 until len) {
                val idx = start + i
                if (idx >= n) break
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-t * 4.5)
                mix[idx] += triangle(f, t) * env * 0.32
            }
            t0 += b * beat
        }
        return toShorts(mix)
    }

    /** Short breath of band-passed-ish noise with a fast decay. */
    private fun synthPoof(): ShortArray {
        val dur = 0.25
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        var prev = 0.0
        for (i in 0 until n) {
            val white = Random.nextDouble(-1.0, 1.0)
            prev = prev * 0.6 + white * 0.4          // crude low-pass → breathy
            val env = (1.0 - i.toDouble() / n)
            out[i] = prev * env * 0.6
        }
        return toShorts(out)
    }

    /** Four ascending triangle notes (C-E-G-C). */
    private fun synthTada(): ShortArray {
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5)
        val gap = 0.06
        val total = (notes.size - 1) * gap + 0.7 + 0.1
        val n = (SAMPLE_RATE * total).toInt()
        val out = DoubleArray(n)
        for ((k, f) in notes.withIndex()) {
            val start = (SAMPLE_RATE * k * gap).toInt()
            val len = (SAMPLE_RATE * 0.7).toInt()
            for (i in 0 until len) {
                val idx = start + i
                if (idx >= n) break
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-t * 5.0)
                out[idx] += triangle(f, t) * env * 0.3
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

    /** Write 16-bit mono PCM as a minimal WAV file. */
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
