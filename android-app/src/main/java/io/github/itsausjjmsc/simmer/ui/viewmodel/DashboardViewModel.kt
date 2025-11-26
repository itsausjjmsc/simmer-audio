package io.github.itsausjjmsc.simmer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.itsausjjmsc.simmer.data.AudioRepository
import io.github.itsausjjmsc.simmer.data.AudioState
import io.github.itsausjjmsc.simmer.core.ControllerState
import io.github.itsausjjmsc.simmer.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val avrVolumeDb: Float = -30f,
    val roomLoudnessDb: Float = -60f,
    val controllerState: ControllerState = ControllerState.IDLE,
    val graphData: List<Float> = emptyList()
)

class DashboardViewModel(
    private val audioRepository: AudioRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Ensure we collect the state safely
            audioRepository.audioState.collect { audioState ->
                _uiState.update { currentState ->
                    val newRoomLoudnessDb = audioState.smoothedRoomLoudnessDb
                    val newControllerState = audioState.controllerState
                    // Keep last 120 points
                    val updatedGraphData = (currentState.graphData + newRoomLoudnessDb).takeLast(120)

                    currentState.copy(
                        roomLoudnessDb = newRoomLoudnessDb,
                        controllerState = newControllerState,
                        graphData = updatedGraphData
                    )
                }
            }
        }
    }
}

class DashboardViewModelFactory(
    private val audioRepository: AudioRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(audioRepository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}