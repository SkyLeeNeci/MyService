package test.karpenko.myservice

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import test.karpenko.myservice.databinding.ActivityMainBinding
import test.karpenko.myservice.services.MainActivityService
import test.karpenko.myservice.services.MainActivityService.Companion.SEEK_BAR_MAX_VALUE
import test.karpenko.myservice.services.MainActivityService.Companion.SEEK_BAR_PROGRESS
import test.karpenko.myservice.services.MainActivityService.Companion.TIMER_RESULT

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var customService: MainActivityService? = null
    private var mBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startPlayer.setOnClickListener {
            if (mBound){
                customService?.startMediaPlayer()
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    customService?.setMediaPlayerSeekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1?.let { intent ->
                val time = intent.getIntExtra(TIMER_RESULT, 0)
                val seekBarMaxValue = intent.getIntExtra(SEEK_BAR_MAX_VALUE, 0)
                val seekBarProgress = intent.getIntExtra(SEEK_BAR_PROGRESS, 0)
                binding.progress.text = getTimeStringFromInt(time)
                binding.seekBar.max = seekBarMaxValue
                binding.seekBar.progress = seekBarProgress
                Log.d(TAG, time.toString())
            }
        }
    }

    private fun getTimeStringFromInt(time: Int): String {
        val seconds = time / 1000
        val minutes = seconds / 60
        val hours = minutes / 3600

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            customService = (p1 as MainActivityService.MainActivityServiceBinder).getService()
            mBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            customService = null
            mBound = false
        }

    }

    override fun onStart() {
        super.onStart()
        Intent(this, MainActivityService::class.java).also {
            startService(it)
            bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        IntentFilter("getMediaPlayerTime").also {
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, it)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        unbindService(serviceConnection)
        customService = null
        mBound = false
    }

    override fun onDestroy() {
        super.onDestroy()
        /*LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        unbindService(serviceConnection)
        customService = null
        mBound = false*/
    }


    companion object{
        private const val TAG = "MainActivity"
    }
}