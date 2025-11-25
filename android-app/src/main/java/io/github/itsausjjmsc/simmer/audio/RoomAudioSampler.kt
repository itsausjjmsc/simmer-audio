package io.github.itsausjjmsc.simmer.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*
import kotlin.math.log10
import kotlin.math.sqrt

@SuppressLint("MissingPermission")
class RoomAudioSampler(
    private val context: Context,
    private val sampleRateHz: Int = 44100,
    private val bufferMillis: Int = 200,
    private val onRmsMeasured: (rmsDb: Float) -> Unit
) {
    private var audioRecord: AudioRecord? = null
    private var samplingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val bufferSize: Int = (sampleRateHz * (bufferMillis / 1000.0)).toInt()
    private val audioBuffer = ShortArray(bufferSize)

    fun start() {
        if (samplingJob?.isActive == true) return

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRateHz,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize * 2 // Bytes
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                // TODO: Handle initialization failure
                return
            }

            audioRecord?.startRecording()

            samplingJob = scope.launch {
                while (isActive) {
                    val readResult = audioRecord?.read(audioBuffer, 0, bufferSize) ?: 0
                    if (readResult > 0) {
                        val rms = calculateRms(audioBuffer, readResult)
                        val rmsDb = convertRmsToDb(rms)
                        onRmsMeasured(rmsDb)
                    }
                    // TODO: Consider a more dynamic delay or yielding
                }
            }
        } catch (e: SecurityException) {
            // TODO: Propagate permission errors to the UI/caller
            e.printStackTrace()
        }
    }

    fun stop() {
        samplingJob?.cancel()
        samplingJob = null

        audioRecord?.apply {
            if (state == AudioRecord.STATE_INITIALIZED) {
                stop()
            }
            release()
        }
        audioRecord = null
    }

    fun isRunning(): Boolean {
        return samplingJob?.isActive == true
    }

    private fun calculateRms(buffer: ShortArray, size: Int): Double {
        var sumOfSquares = 0.0
        for (i in 0 until size) {
            sumOfSquares += buffer[i] * buffer[i]
        }
        return sqrt(sumOfSquares / size)
    }

    private fun convertRmsToDb(rms: Double): Float {
        // TODO: This is a placeholder. A proper dBFS conversion requires a reference value.
        if (rms == 0.0) return -120.0f // Represents silence
        return (20 * log10(rms / Short.MAX_VALUE)).toFloat()
    }
}