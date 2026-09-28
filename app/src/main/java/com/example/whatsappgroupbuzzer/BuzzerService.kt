package com.example.whatsappgroupbuzzer

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat

class BuzzerService : Service() {
    private var alarmPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) { stopAlarm(); stopSelf(); return START_NOT_STICKY }
        createChannel()
        startForeground(NOTIFICATION_ID, alarmNotification(intent?.getStringExtra(EXTRA_GROUP).orEmpty()))
        startAlarm()
        return START_STICKY
    }

    private fun startAlarm() {
        if (alarmPlayer == null) {
            // MediaPlayer supports reliable looping from Android 8 onward; Ringtone looping does not.
            alarmPlayer = runCatching {
                MediaPlayer().apply {
                setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
                setDataSource(this@BuzzerService, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                isLooping = true
                prepare()
                start()
            }
            }.getOrNull()
        }
        vibrator = getSystemService(Vibrator::class.java).also {
            it.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 700, 350), 0))
        }
    }

    private fun alarmNotification(group: String): Notification {
        val stopIntent = Intent(this, BuzzerService::class.java).setAction(ACTION_STOP)
        val stopPending = PendingIntent.getService(this, 10, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("WhatsApp mention: $group")
            .setContentText("Buzzer is running. Tap Stop to silence it.")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .addAction(android.R.drawable.ic_media_pause, getString(R.string.stop_alarm), stopPending)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(CHANNEL_ID, getString(R.string.channel_alarm), NotificationManager.IMPORTANCE_HIGH).apply {
                description = getString(R.string.channel_alarm_description)
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun stopAlarm() {
        alarmPlayer?.run { stop(); release() }; alarmPlayer = null
        vibrator?.cancel(); vibrator = null
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() { stopAlarm(); super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "mention_alarm"
        private const val NOTIFICATION_ID = 101
        private const val ACTION_STOP = "com.example.whatsappgroupbuzzer.STOP"
        private const val EXTRA_GROUP = "group"
        fun start(context: Context, group: String) {
            val intent = Intent(context, BuzzerService::class.java).putExtra(EXTRA_GROUP, group)
            if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(intent) else context.startService(intent)
        }
        fun stop(context: Context) {
            context.startService(Intent(context, BuzzerService::class.java).setAction(ACTION_STOP))
        }
    }
}
