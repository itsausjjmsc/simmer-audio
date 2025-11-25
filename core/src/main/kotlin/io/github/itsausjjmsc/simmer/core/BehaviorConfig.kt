package io.github.itsausjjmsc.simmer.core

data class QuietHoursConfig(
    val enabled: Boolean,
    val startMinutes: Int, // minutes from midnight, 0–1439
    val endMinutes: Int,   // minutes from midnight, can represent overnight ranges
    val nightVolumeMaxDb: Float
)

data class BehaviorConfig(
    val operationMode: OperationMode,
    val silenceTimeoutMs: Long,
    val autoIdleEnabled: Boolean,
    val fadeToSafeOnIdle: Boolean,
    val idleSafeVolumeDb: Float,
    val respectAvrPowerState: Boolean,
    val globalMaxVolumeDb: Float,
    val quietHours: QuietHoursConfig
)