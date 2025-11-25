package io.github.itsausjjmsc.simmer.core

data class Profile(
    val profileName: String,
    val targetLoudnessDb: Float,
    val attackSpeedMs: Int,
    val releaseSpeedMs: Int,
    val smoothingWindowMs: Int,
    val volumeMinDb: Float,
    val volumeMaxDb: Float
)

enum class ControllerState {
    IN_RANGE,
    TOO_LOUD,
    TOO_QUIET,
    PAUSED,
    ERROR,
    IDLE
}

enum class OperationMode {
    AUTO,
    SEMI_AUTO,
    LOGGING_ONLY
}

data class LoudnessState(
    val rmsDb: Float,
    val smoothedDb: Float,
    val timestampMs: Long
)

sealed class VolumeAction {
    data object None : VolumeAction()
    data class Nudge(val deltaDb: Float) : VolumeAction()
    data class SetVolume(val targetDb: Float) : VolumeAction()
}

interface AvrClient {
    suspend fun connect(): Boolean
    suspend fun disconnect()
    suspend fun getCurrentVolumeDb(): Float?
    suspend fun setVolumeDb(targetDb: Float): Boolean
    suspend fun nudgeVolume(deltaDb: Float): Boolean
    suspend fun setMute(muted: Boolean): Boolean
    suspend fun powerOn(): Boolean
    suspend fun powerOff(): Boolean
    suspend fun selectInput(inputId: String): Boolean
    suspend fun getStatusSummary(): String
}