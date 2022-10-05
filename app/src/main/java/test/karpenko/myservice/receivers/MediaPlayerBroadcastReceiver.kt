package test.karpenko.myservice.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import test.karpenko.myservice.services.MainActivityService
import java.text.SimpleDateFormat
import java.util.*

class MediaPlayerBroadcastReceiver : BroadcastReceiver() {

    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

    private val _timerLiveData: MutableLiveData<String> = MutableLiveData()
    val timerLiveData: LiveData<String> = _timerLiveData

    private val _durationLiveData: MutableLiveData<Int> = MutableLiveData()
    val durationLiveData: LiveData<Int> = _durationLiveData

    private val _positionLiveData: MutableLiveData<Int> = MutableLiveData()
    val positionLiveData: LiveData<Int> = _positionLiveData

    override fun onReceive(p0: Context?, p1: Intent?) {
        when(p1?.action){
            MainActivityService.MEDIA_PLAYER_TIME_ACTION -> {
                val milliseconds = p1.getIntExtra(MainActivityService.TIMER_RESULT, 0)
                val time = timeFormatter.format(milliseconds)
                _timerLiveData.postValue(time)

                val duration = p1.getIntExtra(MainActivityService.SEEK_BAR_MAX_VALUE, 0)
                _durationLiveData.postValue(duration)

                val position = p1.getIntExtra(MainActivityService.SEEK_BAR_PROGRESS, 0)
                _positionLiveData.postValue(position)
            }
        }
    }
}