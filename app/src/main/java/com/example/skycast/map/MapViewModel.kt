package com.example.skycast.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.model.remote.WetherForeCastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(private val weatherRepository: WeatherRepositoryImpl) : ViewModel() {

    private val _currentWeather = MutableLiveData<CurrentWetherResponse>()
    val currentWeather: LiveData<CurrentWetherResponse> get() = _currentWeather


    private val _weatherForecast = MutableLiveData<WetherForeCastResponse>()
    val weatherForecast: LiveData<WetherForeCastResponse> get() = _weatherForecast

    private val _currentLocation = MutableLiveData<Pair<Double, Double>>()
    val currentLocation: LiveData<Pair<Double, Double>> get() = _currentLocation



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

            }
        }
    }

    fun setLocation(latitude: Double, longitude: Double, language: String, units: String) {
        _currentLocation.postValue(Pair(latitude, longitude))
        fetchWeather(latitude, longitude, language, units)
    }
}
