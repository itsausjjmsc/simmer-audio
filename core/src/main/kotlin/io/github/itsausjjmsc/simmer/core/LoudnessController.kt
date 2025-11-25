package io.github.itsausjjmsc.simmer.core

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

private const val TARGET_TOLERANCE_DB = 1.5f
private const val BIG_MISMATCH_DB = 8.0f
private const val MAX_SINGLE_STEP_DB = 2.0f
private const val ACTIVE_ARM_THRESHOLD_MS = 1000L

class LoudnessController(
    private val profile: Profile,
    private val behavior: BehaviorConfig,
    private val loudnessEngine: LoudnessEngine
) {
    private var controllerState: ControllerState = ControllerState.IDLE
    private var lastSampleTimeMs: Long = 0L
    private var idleDurationMs: Long = 0L
    private var activeDurationMs: Long = 0L
    private var manualPause: Boolean = false
    private var lastMetrics: LoudnessMetrics? = null
    private var hasFadedToIdleSafe: Boolean = false

    fun onLoudnessSample(rmsDb: Float, timestampMs: Long): VolumeAction {
        val dt = if (lastSampleTimeMs == 0L) 0L else timestampMs - lastSampleTimeMs
        lastSampleTimeMs = timestampMs

        val metrics = loudnessEngine.onSample(rmsDb, timestampMs)
        lastMetrics = metrics

        if (metrics.isSilent) {
            idleDurationMs += dt
            activeDurationMs = 0
        } else {
            idleDurationMs = 0
            activeDurationMs += dt
        }

        val inQuietHours = computeIsInQuietHours(timestampMs)

        // --- Global Overrides ---
        if (manualPause) {
            controllerState = ControllerState.PAUSED
            return VolumeAction.None
        }
        if (behavior.operationMode == OperationMode.LOGGING_ONLY) {
            controllerState = ControllerState.IN_RANGE // Or a new "LOGGING" state
            return VolumeAction.None
        }

        // --- Auto-IDLE Handling ---
        handleAutoIdleAndFade(metrics, inQuietHours)?.let { return it }
        // TODO: In SEMI_AUTO mode, check for an "armed" flag from the UI.

        // --- Loudness Error Calculation ---
        val errorDb = metrics.smoothedDb - profile.targetLoudnessDb
        val absError = abs(errorDb)

        if (absError < TARGET_TOLERANCE_DB) {
            controllerState = ControllerState.IN_RANGE
            return VolumeAction.None
        }

        controllerState = if (errorDb > 0) ControllerState.TOO_LOUD else ControllerState.TOO_QUIET

        // --- Action Decision ---
        if (absError >= BIG_MISMATCH_DB) {
            val safeTarget = clampVolume(profile.targetLoudnessDb, inQuietHours)
            return VolumeAction.SetVolume(safeTarget)
        }

        // --- Nudge Calculation (Attack/Release) ---
        val desiredDeltaDb = -errorDb
        val slopeMsPerDb = if (desiredDeltaDb > 0) profile.attackSpeedMs else profile.releaseSpeedMs
        val maxStepFromSlope = if (slopeMsPerDb > 0) dt.toFloat() / slopeMsPerDb else MAX_SINGLE_STEP_DB

        val step = sign(desiredDeltaDb) * min(abs(desiredDeltaDb), min(maxStepFromSlope, MAX_SINGLE_STEP_DB))

        // Nudge action's delta is relative, so clamping happens on the AVR side based on current volume
        return VolumeAction.Nudge(step)
    }

    private fun handleAutoIdleAndFade(metrics: LoudnessMetrics, inQuietHours: Boolean): VolumeAction? {
        if (!behavior.autoIdleEnabled) {
            hasFadedToIdleSafe = false
            return null
        }

        val isSilentLongEnough = metrics.isSilent
        if (isSilentLongEnough) {
            val previouslyActive = controllerState != ControllerState.IDLE
            controllerState = ControllerState.IDLE
            if (behavior.fadeToSafeOnIdle && previouslyActive && !hasFadedToIdleSafe) {
                hasFadedToIdleSafe = true
                val safeVolume = clampVolume(behavior.idleSafeVolumeDb, inQuietHours)
                return VolumeAction.SetVolume(safeVolume)
            }
            return VolumeAction.None // Stay idle, no action needed.
        }

        // Not silent
        val wasIdle = controllerState == ControllerState.IDLE
        hasFadedToIdleSafe = false
        if (wasIdle && activeDurationMs < ACTIVE_ARM_THRESHOLD_MS) {
            // Just came out of idle, wait a moment before acting to avoid jitter.
            return VolumeAction.None
        }

        return null // Not idle, proceed with normal logic.
    }

    private fun clampVolume(targetDb: Float, inQuietHours: Boolean): Float {
        var clamped = targetDb.coerceIn(profile.volumeMinDb, profile.volumeMaxDb)
        clamped = clamped.coerceAtMost(behavior.globalMaxVolumeDb)
        if (inQuietHours && behavior.quietHours.enabled) {
            clamped = clamped.coerceAtMost(behavior.quietHours.nightVolumeMaxDb)
        }
        return clamped
    }

    private fun computeIsInQuietHours(timestampMs: Long): Boolean {
        // TODO: Implement real time-of-day comparison using java.util.Calendar or similar.
        // Needs to handle start/end times and overnight ranges correctly.
        return false
    }

    // --- Public Accessors ---
    fun getControllerState(): ControllerState = controllerState
    fun getLastMetrics(): LoudnessMetrics? = lastMetrics
    fun setManualPause(paused: Boolean) {
        manualPause = paused
        if (paused) {
            controllerState = ControllerState.PAUSED
        }
    }
}