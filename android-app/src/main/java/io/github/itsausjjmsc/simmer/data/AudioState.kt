package io.github.itsausjjmsc.simmer.data

import io.github.itsausjjmsc.simmer.core.ControllerState

data class AudioState(
    val smoothedRoomLoudnessDb: Float = -60f,
    val controllerState: ControllerState = ControllerState.IDLE
)
