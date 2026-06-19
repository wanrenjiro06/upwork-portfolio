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
import kotlin.random.Random

/**
 * ShellAudio
 * ----------------------------------------------------------------------------
 * Procedurally-synthesized shell-game SFX — NO bundled audio assets, mirroring
 * the web demo's Web Audio approach and the WheelAudio pattern.
 *
 *   whoosh()  — a cup sliding during the shuffle.
 *   lift()    — a cup being lifted at the reveal.
 *   fanfare() — the win jingle.
 *   lose()    — the gentle "not this time" tone.
 */
class ShellAudio(context: Context) {

    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var whooshId = 0
    private var liftId = 0
    private var fanfareId = 0
    private var loseId = 0
    private var loaded = false

    init {
        try {
            val cache = context.cacheDir
            val whooshWav = File(cache, "sh_whoosh.wav")
            val liftWav = File(cache, "sh_lift.wav")
            val fanWav = File(cache, "sh_fanfare.wav")
            val loseWav = File(cache, "sh_lose.wav")
            writeWav(whooshWav, synthWhoosh())
            writeWav(liftWav, synthLift())
            writeWav(fanWav, synthFanfare())
            writeWav(loseWav, synthLose())
            whooshId = pool.load(whooshWav.absolutePath, 1)
            liftId = pool.load(liftWav.absolutePath, 1)
            fanfareId = pool.load(fanWav.absolutePath, 1)
            loseId = pool.load(loseWav.absolutePath, 1)
            pool.setOnLoadCompleteListener { _, _, status -> if (status == 0) loaded = true }
        } catch (e: Exception) {
            // Audio is non-essential; a silent kiosk still works.
        }
    }

    fun whoosh()  { if (loaded) pool.play(whooshId, 0.4f, 0.4f, 1, 0, 1f) }
    fun lift()    { if (loaded) pool.play(liftId, 0.5f, 0.5f, 1, 0, 1f) }
    fun fanfare() { if (loaded) pool.play(fanfareId, 0.85f, 0.85f, 1, 0, 1f) }
    fun lose()    { if (loaded) pool.play(loseId, 0.6f, 0.6f, 1, 0, 1f) }

    fun release() { try { pool.release() } catch (e: Exception) { } }

    // ---- Synthesis ----------------------------------------------------------

    /** Band-passed noise sweep — a cup sliding. */
    private fun synthWhoosh(): ShortArray {
        val dur = 0.2
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        var prev = 0.0
        for (i in 0 until n) {
            val white = Random.nextDouble(-1.0, 1.0)
            prev = prev * 0.7 + white * 0.3
            val env = exp(-(i.toDouble() / n) * 3.0) * (1.0 - i.toDouble() / n)
            out[i] = prev * env * 0.5
        }
        return toShorts(out)
    }

    /** Soft sine pop for a cup lift. */
    private fun synthLift(): ShortArray {
        val dur = 0.16
        val n = (SAMPLE_RATE * dur).toInt()
        val out = DoubleArray(n)
        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 18.0)
            out[i] = sin(2 * PI * 520.0 * t) * env * 0.4
        }
        return toShorts(out)
    }

    /** Four ascending triangle notes. */
    private fun synthFanfare(): ShortArray {
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5)
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

    /** Two descending triangle notes — gentle "aw". */
    private fun synthLose(): ShortArray {
        val notes = doubleArrayOf(330.0, 247.0)
        val gap = 0.14
        val total = gap + 0.45
        val n = (SAMPLE_RATE * total).toInt()
        val out = DoubleArray(n)
        for ((k, f) in notes.withIndex()) {
            val start = (SAMPLE_RATE * k * gap).toInt()
            val len = (SAMPLE_RATE * 0.4).toInt()
            for (i in 0 until len) {
                val idx = start + i
                if (idx >= n) break
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-t * 7.0)
                out[idx] += triangle(f, t) * env * 0.3
            }
        }
        return toShorts(out)
    }

    private fun triangle(freq: Double, t: Double): Double { val p = (t * freq) % 1.0; return 4.0 * abs(p - 0.5) - 1.0 }

    private fun toShorts(buf: DoubleArray): ShortArray {
        val res = ShortArray(buf.size)
        for (i in buf.indices) { val v = buf[i].coerceIn(-1.0, 1.0); res[i] = (v * Short.MAX_VALUE).toInt().toShort() }
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
            for (i in pcm.indices) { bytes[i * 2] = (pcm[i].toInt() and 0xff).toByte(); bytes[i * 2 + 1] = ((pcm[i].toInt() shr 8) and 0xff).toByte() }
            fos.write(bytes)
        }
    }
    private fun intLE(v: Int) = byteArrayOf((v and 0xff).toByte(), ((v shr 8) and 0xff).toByte(), ((v shr 16) and 0xff).toByte(), ((v shr 24) and 0xff).toByte())
    private fun shortLE(v: Int) = byteArrayOf((v and 0xff).toByte(), ((v shr 8) and 0xff).toByte())

    companion object { private const val SAMPLE_RATE = 44_100 }
}
