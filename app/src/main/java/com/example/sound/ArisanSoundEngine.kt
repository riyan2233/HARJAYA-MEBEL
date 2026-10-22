package com.example.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Real-time synthesis audio engine for Harjaya Mebel Arisan.
 * Generates showroom acoustic ambient music, arisan drum-roll tick, and winner celebration fanfare
 * via PCM audio tracks. Zero external dependencies needed!
 */
class ArisanSoundEngine {

  private val sampleRate = 44100
  private var musicJob: Job? = null
  private var isMusicRunning = false
  private var volume = 0.5f

  fun setVolume(vol: Float) {
    this.volume = vol.coerceIn(0f, 1f)
  }

  fun startBackgroundMusic(scope: CoroutineScope) {
    if (isMusicRunning) return
    isMusicRunning = true

    musicJob = scope.launch(Dispatchers.Default) {
      // Warm pentatonic acoustic wood tones (G3, B3, D4, E4, G4, A4, B4, D5)
      val notes = listOf(196.0, 246.94, 293.66, 329.63, 392.00, 440.00, 493.88, 587.33)
      var noteIndex = 0

      while (isActive && isMusicRunning) {
        val freq = notes[noteIndex % notes.size]
        playTone(freq, durationMs = 380, isSoft = true, noteVolume = volume * 0.35f)
        delay(120)

        // Occasional harmonic chord note
        if (noteIndex % 3 == 0) {
          playTone(freq * 1.5, durationMs = 280, isSoft = true, noteVolume = volume * 0.25f)
        }

        noteIndex++
        delay(250)
      }
    }
  }

  fun stopBackgroundMusic() {
    isMusicRunning = false
    musicJob?.cancel()
    musicJob = null
  }

  fun playLotteryTickSound(scope: CoroutineScope) {
    scope.launch(Dispatchers.Default) {
      // Wood-block rattle click
      playTone(600.0 + (Math.random() * 300.0), durationMs = 45, isSoft = false, noteVolume = volume * 0.8f)
    }
  }

  fun playWinnerCelebration(scope: CoroutineScope) {
    scope.launch(Dispatchers.Default) {
      // Festive ascending fanfare: C5, E5, G5, C6 triumphant chime
      val fanfare = listOf(
        523.25 to 150L,
        659.25 to 150L,
        783.99 to 180L,
        1046.50 to 500L
      )
      for ((freq, dur) in fanfare) {
        playTone(freq, dur.toInt(), isSoft = false, noteVolume = volume * 0.9f)
        delay(dur + 30)
      }
    }
  }

  private fun playTone(frequency: Double, durationMs: Int, isSoft: Boolean, noteVolume: Float) {
    try {
      val numSamples = (durationMs * sampleRate) / 1000
      val buffer = ShortArray(numSamples)
      val angularFreq = 2.0 * Math.PI * frequency / sampleRate

      for (i in 0 until numSamples) {
        val t = i.toDouble() / numSamples
        // Envelope: attack & decay
        val envelope = if (isSoft) {
          // Smooth bell envelope
          sin(Math.PI * t) * (1.0 - t * 0.5)
        } else {
          // Percussive pluck envelope
          Math.exp(-4.0 * t)
        }
        val sample = sin(angularFreq * i) * envelope * Short.MAX_VALUE * noteVolume
        buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }

      val minBufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )

      val audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(maxOf(minBufferSize, buffer.size * 2))
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      audioTrack.write(buffer, 0, buffer.size)
      audioTrack.play()

      // Cleanup
      Thread.sleep(durationMs.toLong() + 20)
      audioTrack.stop()
      audioTrack.release()
    } catch (_: Exception) {
      // Non-fatal if audio hardware busy
    }
  }
}
