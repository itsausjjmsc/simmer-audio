package io.github.itsausjjmsc.simmer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"
    const val REMOTE = "remote"
    const val LOG = "log"
}

@Composable
fun SimmerNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.ONBOARDING) { OnboardingScreen() }
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onLogClick = { navController.navigate(Routes.LOG) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Routes.REMOTE) { RemoteScreen() }
        composable(Routes.LOG) {
            LogScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun SimmerApp() {
    io.github.itsausjjmsc.simmer.ui.theme.SimmerAudioTheme {
        SimmerNavHost()
    }
}

// Placeholder Screens
@Composable fun OnboardingScreen() { Text("Onboarding Screen") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onSettingsClick: () -> Unit, onLogClick: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Simmer Audio") },
                actions = {
                    IconButton(onClick = onLogClick) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Session Log"
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Status Icon")
                    Text("ACTIVE", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The "Big Numbers" Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("AVR Volume", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "-30.0 dB",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Room Loudness", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "-24.0 dB",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // State Indicator
            Text(
                "State: IN RANGE",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.weight(1f)) // Pushes controls to the bottom

            // Control Deck
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Pause (15m)")
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Target -1dB")
                }
                FilledTonalButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Target +1dB")
                }
            }
        }
    }
}

@Composable fun RemoteScreen() { Text("Remote Screen") }