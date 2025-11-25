package io.github.itsausjjmsc.simmer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Profiles") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Active Profile Section
            SectionTitle("Active Profile")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Movie (Default)", style = MaterialTheme.typography.titleMedium)
                    Text("Target: -24dB", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Profiles List Section
            SectionTitle("Profiles")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Movie", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Night Mode", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Dialogue Boost", style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Advanced Tuning Section
            SectionTitle("Advanced Tuning")
            TuningSlider("Attack", 0f, 1000f, 200f)
            TuningSlider("Release", 0f, 2000f, 500f)
            TuningSlider("Smoothing", 0f, 5000f, 1000f)


            // System Section
            SectionTitle("System")
            var ipAddress by remember { mutableStateOf("192.168.1.100") }
            OutlinedTextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("AVR IP Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Test Connection")
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun TuningSlider(name: String, rangeStart: Float, rangeEnd: Float, initialValue: Float) {
    var sliderPosition by remember { mutableStateOf(initialValue) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$name: ${sliderPosition.toInt()} ms")
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = rangeStart..rangeEnd
        )
    }
}