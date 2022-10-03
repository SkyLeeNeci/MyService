package test.karpenko.myservice.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import test.karpenko.myservice.R

class MainActivityService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        Log.d(TAG, "Current thread = ${Thread.currentThread().id}")
        if (mediaPlayer == null) {
            startMediaPlayer()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        stopMediaPlayer()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainServiceTAG"
    }

    private fun startMediaPlayer() {
        Thread{
            mediaPlayer = MediaPlayer.create(this, R.raw.test)
            mediaPlayer?.start()
        }.start()

    }
    private fun stopMediaPlayer(){
        mediaPlayer?.stop()
    }
}