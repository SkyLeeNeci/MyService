package test.karpenko.myservice

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import test.karpenko.myservice.databinding.ActivityMainBinding
import test.karpenko.myservice.services.MainActivityService
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startPlayer.setOnClickListener {
            if (this.isMyServiceRunning(MainActivityService::class.java)){
                Intent(this, MainActivityService::class.java).also {
                    stopService(it)
                }
            }else{
                Intent(this, MainActivityService::class.java).also {
                    startService(it)
                }
            }
        }

    }

    private fun Context.isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
                val time = p1?.getIntExtra("TimerResult", 0)
                binding.progress.text = time?.let { getTimeStringFromInt(it) }
                Log.d("MainServiceTAG", time.toString())

        }

    }

    private fun getTimeStringFromInt(time: Int): String {
        val seconds = time / 1000
        val minutes = seconds / 60
        val hours = minutes / 3600

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String {
        return String.format("%02d:%02d:%02d", hours,minutes, seconds)
    }

    override fun onResume() {
        super.onResume()
        IntentFilter("getMediaPlayerTime").also {
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }
}