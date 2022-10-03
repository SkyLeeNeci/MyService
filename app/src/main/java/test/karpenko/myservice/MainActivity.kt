package test.karpenko.myservice

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import test.karpenko.myservice.databinding.ActivityMainBinding
import test.karpenko.myservice.services.MainActivityService

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

}