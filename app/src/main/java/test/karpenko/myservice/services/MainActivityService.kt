package test.karpenko.myservice.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import test.karpenko.myservice.R
import test.karpenko.myservice.utils.NotificationHelper
import java.util.*

class MainActivityService : Service() {

    private val binder = MainActivityServiceBinder()

    private var mediaPlayer: MediaPlayer? = null
    private var timer: Timer = Timer()

    override fun onBind(p0: Intent?): IBinder = binder

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        initPlayerAndReceiveDuration()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        if (intent?.action.equals(ACTION_STOP_SERVICE)){
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    fun setMediaPlayerSeekTo(int: Int) {
        mediaPlayer?.seekTo(int)
    }

    fun startMediaPlayer() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            //stopForeground(false)
        } else {
            NotificationHelper(this, CHANNEL_ID, NOTIFICATION_ID, this).showNotification()
            mediaPlayer?.start()
            setUpTimer()
        }
    }

    private fun initPlayerAndReceiveDuration() {
        mediaPlayer = MediaPlayer.create(this, R.raw.test)
        LocalBroadcastManager.getInstance(this@MainActivityService).sendBroadcast(
            Intent(MEDIA_PLAYER_TIME_ACTION)
                .putExtra(SEEK_BAR_MAX_VALUE, mediaPlayer?.duration)
        )
    }

    private fun setUpTimer() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (mediaPlayer?.isPlaying == true) {
                    val duration = mediaPlayer?.duration
                    val position = mediaPlayer?.currentPosition
                    Log.d(TAG, " Position : $position    Duration: $duration")
                    LocalBroadcastManager.getInstance(this@MainActivityService).sendBroadcast(
                        Intent(MEDIA_PLAYER_TIME_ACTION)
                            .putExtra(TIMER_RESULT, position)
                            .putExtra(SEEK_BAR_MAX_VALUE, duration)
                            .putExtra(SEEK_BAR_PROGRESS, position)
                    )
                } else {
                    pauseMediaPlayer()
                    Log.d(TAG, "ERROR")

                }
            }
        }, 0, 1000)

    }

    private fun pauseMediaPlayer() {
        mediaPlayer?.pause()
        timer.cancel()
        timer = Timer()
    }


   /* @SuppressLint("ObsoleteSdkInt")
    private fun showNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.ешеду))
            .setContentText("Content Text")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Channel if version > O",
                NotificationManager.IMPORTANCE_NONE
            )
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(NOTIFICATION_ID, notification)
    }*/

    inner class MainActivityServiceBinder : Binder() {
        fun getService(): MainActivityService = this@MainActivityService
    }

    companion object {
        private const val TAG = "MainServiceTAG"
        private const val CHANNEL_ID = "123"
        private const val NOTIFICATION_ID = 123
        const val TIMER_RESULT = "TimerResult"
        const val SEEK_BAR_MAX_VALUE = "SeekBarMaxValue"
        const val SEEK_BAR_PROGRESS = "SeekBarProgress"
        const val MEDIA_PLAYER_TIME_ACTION = "getMediaPlayerTime"
        const val ACTION_STOP_SERVICE = "STOP"
    }

}