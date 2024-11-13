package com.example.skycast.map.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.setting.SettingsManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.util.Log

class MapViewModel(private val weatherRepository: WeatherRepositoryImpl, private val context: Context) : ViewModel() {

    private val _currentWeather = MutableLiveData<CurrentWetherResponse>()
    val currentWeather: LiveData<CurrentWetherResponse> get() = _currentWeather

    private val _weatherForecast = MutableLiveData<WeatherForecastResponse>()
    val weatherForecast: LiveData<WeatherForecastResponse> get() = _weatherForecast

    private val _currentLocation = MutableLiveData<Pair<Double, Double>>()
    val currentLocation: LiveData<Pair<Double, Double>> get() = _currentLocation

    private val sharedPreferences = SettingsManager(context)
    private val language: String = sharedPreferences.getLanguage() ?: "en"
    private val unit: String = sharedPreferences.getUnit()

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                // Collect current weather flow
                weatherRepository.getCurrentWeather(latitude, longitude, language, unit).collect { currentWeatherResponse ->
                    _currentWeather.postValue(currentWeatherResponse)
                }

                // Collect forecast weather flow
                weatherRepository.getWeatherForecast(latitude, longitude, language, unit).collect { forecastResponse ->
                    _weatherForecast.postValue(forecastResponse)
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error fetching weather data: ${e.message}")
            }
        }
    }

    fun setLocation(latitude: Double, longitude: Double) {
        _currentLocation.postValue(Pair(latitude, longitude))
        fetchWeather(latitude, longitude)
    }
}
