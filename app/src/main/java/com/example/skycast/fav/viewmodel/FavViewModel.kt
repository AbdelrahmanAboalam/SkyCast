package com.example.skycast.fav.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.setting.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavViewModel(private val repository: WeatherRepository, private val context: Context) : ViewModel() {

    private val _currentWeatherList = MutableStateFlow<List<CurrentWetherResponse>>(emptyList())
    val currentWeatherList: StateFlow<List<CurrentWetherResponse>> get() = _currentWeatherList

    private val _currentGetWeather = MutableStateFlow<CurrentWetherResponse?>(null)
    val currentGetWeather: StateFlow<CurrentWetherResponse?> get() = _currentGetWeather

    private val _currentGetForCastWeather = MutableStateFlow<WeatherForecastResponse?>(null)
    val currentGetForCastWeather: StateFlow<WeatherForecastResponse?> get() = _currentGetForCastWeather

    fun fetchAllCurrentWeather() {
        viewModelScope.launch {
            if (isNetworkAvailable(context)) {
                repository.getAllCurrent().collect { weatherData ->
                    _currentWeatherList.value = weatherData
                }
            }
        }
    }

    fun deleteCurrentWeather(weather: CurrentWetherResponse) {
        viewModelScope.launch {
            repository.deleteCurrentWeather(weather)
            repository.deleteWeather(repository.getWeatherById2(weather.idKey))

        }
    }


    fun getCurrentWeatherById(idKey: Int) {
        viewModelScope.launch {
            _currentGetWeather.value = repository.getCurrentWeatherById(idKey)
            _currentGetForCastWeather.value = repository.getWeatherById(idKey)
        }
    }

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