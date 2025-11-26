package io.github.itsausjjmsc.simmer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import io.github.itsausjjmsc.simmer.data.AppContainer
import io.github.itsausjjmsc.simmer.data.DefaultAppContainer

class SimmerApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "simmer_service_channel"
            val channelName = "Simmer Service"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notification channel for Simmer Foreground Service"
                setSound(null, null) // No sound
                enableVibration(false) // No vibration
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
