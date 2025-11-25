package io.github.itsausjjmsc.simmer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private data class LogEntry(val time: String, val message: String)

private val fakeLogs = listOf(
    LogEntry("10:42:15 PM", "Loud scene detected. Lowered volume by -2.0 dB."),
    LogEntry("10:42:10 PM", "System stable. Loudness in range."),
    LogEntry("10:41:55 PM", "Dialogue detected. Raised volume by +1.5 dB."),
    LogEntry("10:41:40 PM", "Too quiet for an extended period. Raised volume by +1.0 dB."),
    LogEntry("10:40:30 PM", "System stable. Loudness in range."),
    LogEntry("10:39:50 PM", "Loud explosion in content. Lowered volume by -4.0 dB."),
    LogEntry("10:39:20 PM", "AVR connection lost. ERROR: Could not reach 192.168.1.100."),
    LogEntry("10:39:10 PM", "System stable. Loudness in range.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Log") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Recent auto-volume events",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            fakeLogs.forEach { log ->
                LogItem(log)
            }
        }
    }
}

@Composable
private fun LogItem(log: LogEntry) {
    val (containerColor, contentColor) = when {
        "Loud" in log.message || "Lowered" in log.message ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "Raised" in log.message || "Too Quiet" in log.message ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "ERROR" in log.message ->
            MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        else -> // Stable
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = log.message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = log.time,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}