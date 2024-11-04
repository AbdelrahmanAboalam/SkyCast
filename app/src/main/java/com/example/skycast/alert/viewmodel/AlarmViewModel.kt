package com.example.skycast.alert.viewmodel

import android.app.PendingIntent
import android.content.Context
import android.util.Log
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

    private val _alarm2 = MutableLiveData<List<Alarm>>(mutableListOf())
    val alarm2: LiveData<List<Alarm>> = _alarm2

    private val sharedPreferences = SettingsManager(context)
    private val language: String = sharedPreferences.getLanguage() ?: "en"
    private val unit: String = sharedPreferences.getUnit()

    val pendingIntentMap = mutableMapOf<Int, PendingIntent>()

    // Function to add alarm
    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch {
            try {
                val currentAlarms = _alarms.value ?: mutableListOf() // Initialize if null
                currentAlarms.add(alarm)

                _alarms.value = currentAlarms
            } catch (e: Exception) {
                Log.e("AlarmViewModel", "Error adding alarm: ${e.message}")
            }
        }
    }
    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch {
            weatherRepository.insertAlarm(alarm)
            fetchAlarms()
        }
    }



    // Function to fetch all alarms
    fun fetchAlarms() {
        viewModelScope.launch {
            _alarms.value = weatherRepository.getAllAlarms().toMutableList() // Fetch from database
        }
    }

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val currentWeatherResponse = weatherRepository.getCurrentWeather(latitude, longitude, language, unit)
                _currentWeather.postValue(currentWeatherResponse)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    // Function to delete alarm
    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            weatherRepository.deleteAlarm(alarm) // Delete from the database
            alarms.value?.let {
                val updatedAlarms = it.toMutableList().apply { remove(alarm) }
                _alarms.value = updatedAlarms // Update LiveData
                // Optionally, notify adapter here if using a local adapter
            }

            // Cancel the PendingIntent associated with the alarm
            val requestCode = alarm.name.hashCode() // Assuming the name is unique
            pendingIntentMap[requestCode]?.cancel() // Cancel the PendingIntent
            pendingIntentMap.remove(requestCode) // Remove it from the map
        }
    }
    fun savePendingIntent(requestCode: Int, pendingIntent: PendingIntent) {
        pendingIntentMap[requestCode] = pendingIntent
    }
}
