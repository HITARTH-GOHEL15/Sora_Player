package com.example.soraplayer.MusicPlayer.MusicService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.exoplayer.ExoPlayer
import com.example.soraplayer.MainActivity
import com.example.soraplayer.R

class MusicService : Service() {

    private lateinit var player: ExoPlayer

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "ACTION_START_FOREGROUND") {
            startForegroundServiceWithNotification()
        }
        return START_STICKY
    }

    private fun startForegroundServiceWithNotification() {
        // Create notification channel for Android O and above
        val channelId = "MusicServiceChannel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create a notification with a PendingIntent to launch your main activity
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Playing Music")
            .setContentText("Your song is playing in the background")
            .setSmallIcon(R.drawable.main_logo_sp) // Replace with your icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        player.release()
        stopForeground(true)
        super.onDestroy()
    }

    fun setPlayer(exoPlayer: ExoPlayer) {
        player = exoPlayer
    }
}

