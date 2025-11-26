package io.github.itsausjjmsc.simmer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.github.itsausjjmsc.simmer.service.SimmerForegroundService
import io.github.itsausjjmsc.simmer.SimmerApplication
import io.github.itsausjjmsc.simmer.ui.SimmerNavHost
import io.github.itsausjjmsc.simmer.ui.theme.SimmerAudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get the application instance
        val simmerApplication = application as SimmerApplication

        setContent {
            val context = LocalContext.current

            val initialGranted = remember {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            }

            var hasMicPermission by remember { mutableStateOf(initialGranted) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                hasMicPermission = granted
            }

            LaunchedEffect(Unit) {
                if (!hasMicPermission) {
                    // Ask on first launch
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }

            LaunchedEffect(hasMicPermission) {
                if (hasMicPermission) {
                    val serviceIntent = Intent(context, SimmerForegroundService::class.java)
                    ContextCompat.startForegroundService(context, serviceIntent)
                }
            }

            if (hasMicPermission) {
                SimmerAudioTheme {
                    // Pass the application instance itself
                    SimmerNavHost(appContainer = simmerApplication)
                }
            } else {
                MicrophoneRequiredScreen(
                    onRequestPermission = {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                )
            }
        }
    }
}

@Composable
fun MicrophoneRequiredScreen(
    onRequestPermission: () -> Unit
) {
    MaterialTheme { // Using MaterialTheme for consistency, as SimmerApp does not accept content
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎤",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Microphone Required",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Simmer needs microphone access to measure room loudness.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Button(onClick = onRequestPermission) {
                    Text("Grant permission")
                }
                Text(
                    text = "If you previously denied this, enable Microphone in system settings.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MicrophoneRequiredScreenPreview() {
    MicrophoneRequiredScreen(onRequestPermission = {})
}
