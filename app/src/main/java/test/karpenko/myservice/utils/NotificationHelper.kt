package test.karpenko.myservice.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import test.karpenko.myservice.MainActivity
import test.karpenko.myservice.R
import test.karpenko.myservice.services.MainActivityService.Companion.ACTION_STOP_SERVICE

class NotificationHelper(
    private val context: Context,
    private val channelId: String,
    private val notificationId: Int,
    private val service: Service
) {

    @SuppressLint("ObsoleteSdkInt", "UnspecifiedImmutableFlag")
    fun showNotification() {

        val openActivityIntent: PendingIntent =
            Intent(context, MainActivity::class.java).let {
                PendingIntent.getActivity(
                    context,
                    0,
                    it,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val closeServiceIntent: PendingIntent =
            Intent(context, service::class.java).let {
                PendingIntent.getService(
                    service,
                    0,
                    it.setAction(ACTION_STOP_SERVICE) ,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Title")
            .setContentText("Content Text")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openActivityIntent)
            .addAction(R.drawable.ic_close, "Stop Service",closeServiceIntent)
            .build()
        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Channel if version > O",
                NotificationManager.IMPORTANCE_NONE
            )
            notificationManager.createNotificationChannel(channel)
        }

        service.startForeground(notificationId, notification)
    }

}