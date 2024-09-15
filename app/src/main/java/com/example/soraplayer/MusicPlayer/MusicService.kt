package com.example.soraplayer.MusicPlayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.soraplayer.Model.MusicItem
import com.example.soraplayer.R

class MusicService : android.app.Service() {

    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()

        // Initialize MediaSession
        if (mediaSession == null) {
            mediaSession = MediaSession.Builder(this, player)
                .setId("com.example.soraplayer.MusicPlayer.session")
                .build()
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    val notification = createNotification()
                    startForeground(NOTIFICATION_ID, notification)
                } else {
                    stopForeground(false)
                }
            }
        })
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val track: MusicItem? = intent.getParcelableExtra(EXTRA_TRACK)
                if (track != null) {
                    playTrack(track)
                }
                player.play()
            }
            ACTION_PAUSE -> {
                player.pause()
            }
            ACTION_SEEK_FORWARD -> player.seekForward()
            ACTION_SEEK_BACKWARD -> player.seekBack()
            ACTION_STOP -> {
                player.stop()
                stopSelf()
            }
        }
        return START_STICKY
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    private fun playTrack(track: MusicItem) {
        val mediaItem = MediaItem.fromUri(track.uri)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun createNotification(): Notification {
        val channelId = "music_channel"
        val channelName = "Music Playback"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        // Pending Intent to open the MusicPlayerActivity when the notification is tapped
        val openAppIntent = Intent(this, MusicPlayerActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action for play/pause
        val playPauseIntent = Intent(this, MusicService::class.java).apply {
            action = if (player.isPlaying) ACTION_PAUSE else ACTION_PLAY
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action for seek forward
        val forwardIntent = Intent(this, MusicService::class.java).apply { action = ACTION_SEEK_FORWARD }
        val forwardPendingIntent = PendingIntent.getService(
            this, 1, forwardIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action for seek backward
        val backwardIntent = Intent(this, MusicService::class.java).apply { action = ACTION_SEEK_BACKWARD }
        val backwardPendingIntent = PendingIntent.getService(
            this, 2, backwardIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Playing ${player.currentMediaItem?.mediaMetadata?.title ?: "Music"}")
            .setSmallIcon(R.drawable.music_note_24dp_e8eaed_fill0_wght400_grad0_opsz24)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.view_list_24dp_e8eaed_fill0_wght400_grad0_opsz24, "Back", backwardPendingIntent)
            .addAction(
                if (player.isPlaying) R.drawable.pause_24dp_e8eaed_fill0_wght400_grad0_opsz24 else R.drawable.play_arrow_24dp_e8eaed_fill0_wght400_grad0_opsz24,
                if (player.isPlaying) "Pause" else "Play",
                playPausePendingIntent
            )
            .addAction(R.drawable.keyboard_double_arrow_right_24dp_e8eaed_fill0_wght400_grad0_opsz24, "Forward", forwardPendingIntent)
            .setStyle(
                mediaSession?.let { MediaStyleNotificationHelper.MediaStyle(it) }
            )
            .build()
    }

    override fun onDestroy() {
        player.release()
        mediaSession?.release()
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_PLAY = "com.example.soraplayer.MusicService.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.soraplayer.MusicService.ACTION_PAUSE"
        const val ACTION_STOP = "com.example.soraplayer.MusicService.ACTION_STOP"
        const val ACTION_SEEK_FORWARD = "com.example.soraplayer.MusicService.ACTION_SEEK_FORWARD"
        const val ACTION_SEEK_BACKWARD = "com.example.soraplayer.MusicService.ACTION_SEEK_BACKWARD"
        const val EXTRA_TRACK = "com.example.soraplayer.MusicService.EXTRA_TRACK"
        const val NOTIFICATION_ID = 1
    }
}
