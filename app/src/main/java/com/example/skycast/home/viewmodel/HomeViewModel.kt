package com.example.skycast.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    // LiveData to hold the current weather and forecast data
    private val _currentWeather = MutableLiveData<CurrentWetherResponse>()
    val currentWeather: LiveData<CurrentWetherResponse> get() = _currentWeather

    private val _weatherForecast = MutableLiveData<WeatherForecastResponse>()
    val weatherForecast: LiveData<WeatherForecastResponse> get() = _weatherForecast

    // Method to fetch weather data for a given latitude and longitude
    fun fetchWeather(latitude: Double, longitude: Double, language: String , units: String ) {
        viewModelScope.launch {
            try {
                // Fetch current weather
                val currentWeatherResponse = weatherRepository.getCurrentWeather(latitude, longitude , language , units)
                _currentWeather.postValue(currentWeatherResponse)

                // Fetch weather forecast
                val forecastResponse = weatherRepository.getWeatherForecast(latitude, longitude, language , units)
                _weatherForecast.postValue(forecastResponse)

            } catch (e: Exception) {
                // Handle exceptions here (e.g., log the error or notify the user)
                // Optionally, you can set error messages to LiveData to display in the UI
            }
        }
    }
}

