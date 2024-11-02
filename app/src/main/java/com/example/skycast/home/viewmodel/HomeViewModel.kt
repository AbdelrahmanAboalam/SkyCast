package com.example.skycast.home.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.setting.SettingsManager
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository,context: Context) : ViewModel() {

    private val _currentWeather = MutableLiveData<CurrentWetherResponse>()
    val currentWeather: LiveData<CurrentWetherResponse> get() = _currentWeather

    private val _weatherForecast = MutableLiveData<WeatherForecastResponse>()
    val weatherForecast: LiveData<WeatherForecastResponse> get() = _weatherForecast

    private val _currentWeatherByCity = MutableLiveData<CurrentWetherResponse>()
    val currentWeatherByCity: LiveData<CurrentWetherResponse> get() = _currentWeatherByCity


    private val sharedPreferences = SettingsManager(context)
    private val language: String = sharedPreferences.getLanguage()
    private val unit: String = sharedPreferences.getUnit()

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val currentWeatherResponse =
                    weatherRepository.getCurrentWeather(latitude, longitude, language, unit)
                _currentWeather.postValue(currentWeatherResponse)

                val forecastResponse =
                    weatherRepository.getWeatherForecast(latitude, longitude, language, unit)
                _weatherForecast.postValue(forecastResponse)

            } catch (e: Exception) {

            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun fetchWeatherByCity(cityName: String) {
        viewModelScope.launch {
            try {
                val currentWeatherResponse =
                    weatherRepository.getCurrentWeatherByCity(cityName, language, unit)
                   _currentWeatherByCity.postValue(currentWeatherResponse)
                fetchWeather(currentWeatherResponse.coord.lat, currentWeatherResponse.coord.lon)

            } catch (e: Exception) {

            }
        }
    }

}

