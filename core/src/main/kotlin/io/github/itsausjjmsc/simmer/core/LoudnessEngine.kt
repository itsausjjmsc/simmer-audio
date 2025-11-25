package io.github.itsausjjmsc.simmer.core

/**
 * Represents the output of the [LoudnessEngine] after processing a sample.
 * This is a pure data holder with no platform-specific dependencies.
 *
 * @property rmsDb The instantaneous, unsmoothed RMS loudness in decibels.
 * @property smoothedDb The smoothed loudness value after applying the exponential moving average.
 * @property isSilent True if the audio has been below the silence threshold for the required duration.
 */
data class LoudnessMetrics(
    val rmsDb: Float,
    val smoothedDb: Float,
    val isSilent: Boolean
)

/**
 * A platform-agnostic engine for processing real-time loudness data in decibels.
 *
 * This class is responsible for:
 * 1. Applying exponential smoothing to raw RMS dB values to get a more stable loudness reading.
 * 2. Detecting periods of effective silence based on configurable thresholds and durations.
 *
 * It does NOT know about Android, AVRs, volume control, or application-level profiles. It is
 * a pure computational component that forms the "ears" of the Simmer Audio system.
 *
 * @param smoothingWindowMs The time window over which to smooth loudness changes, in milliseconds.
 * @param silenceThresholdDb The loudness level (in dB) below which the signal is considered "background noise".
 * @param silenceTimeoutMs The duration (in ms) the signal must remain below [silenceThresholdDb] to be considered "silent".
 */
class LoudnessEngine(
    private val smoothingWindowMs: Long,
    private val silenceThresholdDb: Float,
    private val silenceTimeoutMs: Long
) {
    private var lastSmoothedDb: Float? = null
    private var lastTimestampMs: Long? = null
    private var belowThresholdDurationMs: Long = 0L

    /**
     * Processes a single raw RMS sample and returns the calculated loudness metrics.
     *
     * @param rmsDb The raw RMS value for the latest audio buffer, in decibels.
     * @param timestampMs The timestamp when the sample was captured.
     * @return A [LoudnessMetrics] object containing the instantaneous, smoothed, and silence status.
     */
    fun onSample(rmsDb: Float, timestampMs: Long): LoudnessMetrics {
        val currentSmoothedDb: Float
        val dt = lastTimestampMs?.let { (timestampMs - it).coerceAtLeast(0L) } ?: 0L

        val lastDb = lastSmoothedDb
        if (lastDb == null) {
            // First sample, no smoothing possible yet.
            currentSmoothedDb = rmsDb
        } else {
            // Apply exponential smoothing.
            val alpha = if (smoothingWindowMs <= 0) {
                1.0f // No smoothing, instantly adopt the new value.
            } else {
                (dt.toFloat() / smoothingWindowMs.toFloat()).coerceIn(0f, 1f)
            }
            currentSmoothedDb = lastDb + alpha * (rmsDb - lastDb)
        }

        // Update internal state for the next sample.
        lastSmoothedDb = currentSmoothedDb
        lastTimestampMs = timestampMs

        // Update silence detection state.
        if (currentSmoothedDb < silenceThresholdDb) {
            belowThresholdDurationMs += dt
        } else {
            belowThresholdDurationMs = 0L
        }

        val isSilent = silenceTimeoutMs > 0 && belowThresholdDurationMs >= silenceTimeoutMs

        return LoudnessMetrics(
            rmsDb = rmsDb,
            smoothedDb = currentSmoothedDb,
            isSilent = isSilent
        )
    }
}