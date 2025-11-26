package io.github.itsausjjmsc.simmer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.itsausjjmsc.simmer.SimmerApplication
import io.github.itsausjjmsc.simmer.ui.viewmodel.DashboardViewModel
import io.github.itsausjjmsc.simmer.ui.viewmodel.DashboardViewModelFactory

object Routes {
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"
    const val LOGS = "logs"
}

@Composable
fun SimmerNavHost(appContainer: SimmerApplication) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(
                    audioRepository = appContainer.appContainer.audioRepository,
                    settingsRepository = appContainer.appContainer.settingsRepository
                )
            )
            val uiState by viewModel.uiState.collectAsState()

            DashboardScreen(
                avrVolumeDb = uiState.avrVolumeDb,
                roomLoudnessDb = uiState.roomLoudnessDb,
                controllerState = uiState.controllerState,
                graphData = uiState.graphData,
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onLogClick = { navController.navigate(Routes.LOGS) } // FIXED: Singular 'onLogClick'
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.LOGS) {
            LogScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}