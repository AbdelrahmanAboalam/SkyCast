package com.example.skycast.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.network.FetchingState
import com.example.skycast.setting.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    context: Context
) : ViewModel() {

    // StateFlows for fetching states
    private val _currentWeatherState = MutableStateFlow<FetchingState>(FetchingState.LoadingCurrent)
    val currentWeatherState: StateFlow<FetchingState> get() = _currentWeatherState

    private val _weatherForecastState = MutableStateFlow<FetchingState>(FetchingState.LoadingForecast)
    val weatherForecastState: StateFlow<FetchingState> get() = _weatherForecastState

    // SharedPreferences handling for language and unit
    private val sharedPreferences = SettingsManager(context)
    private val language: String = sharedPreferences.getLanguage()
    private val unit: String = sharedPreferences.getUnit()

    // Function to fetch current weather by coordinates
    fun fetchCurrentWeather(latitude: Double, longitude: Double) {
        _currentWeatherState.value = FetchingState.LoadingCurrent
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(latitude, longitude, language, unit)
                .catch { e ->
                    _currentWeatherState.value = FetchingState.ErrorCurrent("Error fetching current weather: ${e.message}")
                }
                .collect { currentWeatherResponse ->
                    _currentWeatherState.value = FetchingState.SuccessCurrent(currentWeatherResponse)
                }
        }
    }

    // Function to fetch weather forecast by coordinates
    fun fetchWeatherForecast(latitude: Double, longitude: Double) {
        _weatherForecastState.value = FetchingState.LoadingForecast
        viewModelScope.launch {
            weatherRepository.getWeatherForecast(latitude, longitude, language, unit)
                .catch { e ->
                    _weatherForecastState.value = FetchingState.ErrorForecast("Error fetching weather forecast: ${e.message}")
                }
                .collect { forecastResponse ->
                    _weatherForecastState.value = FetchingState.SuccessForecast(forecastResponse)
                }
        }
    }

    // Function to fetch current weather by city name
    fun fetchWeatherByCity(cityName: String) {
        _currentWeatherState.value = FetchingState.LoadingCurrent
        viewModelScope.launch {
            weatherRepository.getCurrentWeatherByCity(cityName, language, unit)
                .catch { e ->
                    _currentWeatherState.value = FetchingState.ErrorCurrent("Error fetching weather by city: ${e.message}")
                }
                .collect { currentWeatherResponse ->
                    _currentWeatherState.value = FetchingState.SuccessCurrent(currentWeatherResponse)
                    // Fetch forecast using coordinates from the city response
                    fetchWeatherForecast(currentWeatherResponse.coord.lat, currentWeatherResponse.coord.lon)
                }
        }
    }
}
