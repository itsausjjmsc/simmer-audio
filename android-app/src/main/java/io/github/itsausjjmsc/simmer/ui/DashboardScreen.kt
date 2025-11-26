package io.github.itsausjjmsc.simmer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.List // Changed to automirrored
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.itsausjjmsc.simmer.core.ControllerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    avrVolumeDb: Float,
    roomLoudnessDb: Float,
    controllerState: ControllerState,
    graphData: List<Float>,
    onSettingsClick: () -> Unit,
    onLogClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Simmer Audio") },
                actions = {
                    IconButton(onClick = onLogClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List, // Changed to AutoMirrored.Filled.List
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
                        text = "${String.format("%.1f", avrVolumeDb)} dB",
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
                        text = "${String.format("%.1f", roomLoudnessDb)} dB",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // State Indicator
            Text(
                "State: ${controllerState.name.replace("_", " ")}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(24.dp))
            Text("Loudness History (120 samples)", style = MaterialTheme.typography.labelSmall)
            LoudnessGraph(
                data = graphData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) // Optional background
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

@Composable
fun LoudnessGraph(
    data: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val minDb = -80f
        val maxDb = 0f
        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1)

        val path = Path()

        data.forEachIndexed { index, dbValue ->
            // Normalize dB to 0..1 (0=bottom, 1=top)
            // dbValue is -60..0.
            // If db = -80, t = 0. If db = 0, t = 1.
            val clamped = dbValue.coerceIn(minDb, maxDb)
            val t = (clamped - minDb) / (maxDb - minDb)

            // Canvas Y is inverted (0 is top)
            val x = index * stepX
            val y = height * (1f - t)

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = androidx.compose.ui.graphics.Color.Cyan, // Or your theme color
            style = Stroke(width = 3.dp.toPx())
        )
    }
}