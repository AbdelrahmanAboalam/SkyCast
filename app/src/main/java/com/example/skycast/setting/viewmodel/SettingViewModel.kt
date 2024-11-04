package com.example.skycast.setting.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.setting.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: WeatherRepository, private val context: Context) : ViewModel() {


    private  var sharedPreferences: SettingsManager

    init {
        sharedPreferences = SettingsManager(context)

    }

    // Function to fetch and update all settings
    fun fetchAllSettings() {
        viewModelScope.launch {
            if (isNetworkAvailable(context)) {
                repository.getAllCurrent().collect { settingsData ->

                    settingsData.forEach { setting ->
                        fetchAndUpdateSetting(setting)
                    }
                }
            }
        }
    }

    private suspend fun fetchAndUpdateSetting(settingData: CurrentWetherResponse) {
        if (isNetworkAvailable(context)) {
            val settingResponse = repository.getCurrentWeather(
                settingData.coord.lat,
                settingData.coord.lon,
                sharedPreferences.getLanguage(),
                sharedPreferences.getUnit()
            )
            settingResponse.idKey = settingData.idKey
            repository.updateCurrentWeather(settingResponse)
            val forecastResponse = repository.getWeatherForecast(
                settingData.coord.lat,
                settingData.coord.lon,
                sharedPreferences.getLanguage(),
                sharedPreferences.getUnit()
            )
            forecastResponse.idKey = settingData.idKey
            repository.updateWeather(forecastResponse)
        }
    }

    // Function to check network availability
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}
