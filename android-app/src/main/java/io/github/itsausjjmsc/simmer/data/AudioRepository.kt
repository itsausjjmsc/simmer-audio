package io.github.itsausjjmsc.simmer.data

import io.github.itsausjjmsc.simmer.core.ControllerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface AudioRepository {
    val audioState: StateFlow<AudioState>

    fun updateFromEngine(
        smoothedRoomLoudnessDb: Float,
        controllerState: ControllerState
    )
}

// Placeholder implementation for now
class DefaultAudioRepository : AudioRepository {
    private val _audioState = MutableStateFlow(AudioState())
    override val audioState: StateFlow<AudioState> = _audioState

    override fun updateFromEngine(
        smoothedRoomLoudnessDb: Float,
        controllerState: ControllerState
    ) {
        _audioState.update { current ->
            current.copy(
                smoothedRoomLoudnessDb = smoothedRoomLoudnessDb,
                controllerState = controllerState
            )
        }
    }
}