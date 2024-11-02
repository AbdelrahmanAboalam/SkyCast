package com.example.skycast.alert.viewmodel

import android.app.PendingIntent
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.alert.view.Alarm
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.setting.SettingsManager
import kotlinx.coroutines.launch

class AlarmViewModel(
    private val weatherRepository: WeatherRepository,
    context: Context
) : ViewModel() {

    private val _alarms = MutableLiveData<MutableList<Alarm>>(mutableListOf())
    val alarms: LiveData<MutableList<Alarm>> = _alarms

    private val _currentWeather = MutableLiveData<CurrentWetherResponse>()
    val currentWeather: LiveData<CurrentWetherResponse> get() = _currentWeather

    private val sharedPreferences = SettingsManager(context)
    private val language: String = sharedPreferences.getLanguage() ?: "en"
    private val unit: String = sharedPreferences.getUnit()

    // Function to add alarm
    fun addAlarm(alarm: Alarm) {
        _alarms.value?.add(alarm)
        _alarms.value = _alarms.value
    }

    val pendingIntentMap = mutableMapOf<Int, PendingIntent>()

    fun savePendingIntent(requestCode: Int, pendingIntent: PendingIntent) {
        // Save the PendingIntent in the HashMap with the request code as the key
        pendingIntentMap[requestCode] = pendingIntent
    }

    // Function to fetch weather data
    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val currentWeatherResponse = weatherRepository.getCurrentWeather(latitude, longitude,language,unit)
                _currentWeather.postValue(currentWeatherResponse)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
