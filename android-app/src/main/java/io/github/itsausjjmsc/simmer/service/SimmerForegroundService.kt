package io.github.itsausjjmsc.simmer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.github.itsausjjmsc.simmer.R
import io.github.itsausjjmsc.simmer.audio.RoomAudioSampler
import io.github.itsausjjmsc.simmer.core.*

class SimmerForegroundService : Service() {

    private val CHANNEL_ID = "simmer_channel"
    private val NOTIFICATION_ID = 1

    private var sampler: RoomAudioSampler? = null
    private var controller: LoudnessController? = null
    private var behaviorConfig: BehaviorConfig? = null
    private var loudnessEngine: LoudnessEngine? = null


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // 1) Behavior configuration (temporary hardcoded defaults)
        behaviorConfig = BehaviorConfig(
            operationMode = OperationMode.AUTO,
            silenceTimeoutMs = 60_000L,          // 60s of low noise before we consider it "silent"
            autoIdleEnabled = true,
            fadeToSafeOnIdle = true,
            idleSafeVolumeDb = -40f,
            respectAvrPowerState = true,
            globalMaxVolumeDb = -12f,
            quietHours = QuietHoursConfig(
                enabled = false,
                startMinutes = 23 * 60,          // 23:00
                endMinutes = 7 * 60,             // 07:00
                nightVolumeMaxDb = -25f
            )
        )

        // TODO: This should come from user settings later
        val activeProfile = Profile(
            profileName = "Default Movie",
            targetLoudnessDb = -25.0f,
            attackSpeedMs = 300,
            releaseSpeedMs = 1500,
            smoothingWindowMs = 800,
            volumeMinDb = -50.0f,
            volumeMaxDb = -15.0f
        )

        // 2) Loudness engine (smoothing + silence detection)
        loudnessEngine = LoudnessEngine(
            smoothingWindowMs = activeProfile.smoothingWindowMs.toLong(),
            silenceThresholdDb = -60f,
            silenceTimeoutMs = behaviorConfig!!.silenceTimeoutMs
        )

        // 3) Controller
        controller = LoudnessController(
            profile = activeProfile,
            behavior = behaviorConfig!!,
            loudnessEngine = loudnessEngine!!
        )

        // 4) Mic sampler – feeds RMS dB into controller
        sampler = RoomAudioSampler(
            context = applicationContext,
            sampleRateHz = 44_100,
            bufferMillis = 200
        ) { rmsDb ->
            val now = System.currentTimeMillis()
            val action = controller?.onLoudnessSample(rmsDb, now) ?: VolumeAction.None

            // For now, just log the decision. Actual AVR control comes later.
            if (action !is VolumeAction.None) {
                Log.d("SimmerForegroundService", "VolumeAction from controller: $action")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Simmer Audio")
            .setContentText("Auto-volume service running")
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with a real icon
            .build()

        startForeground(NOTIFICATION_ID, notification)

        sampler?.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sampler?.stop()
        sampler = null
        controller = null
        loudnessEngine = null
        behaviorConfig = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Simmer Audio Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}