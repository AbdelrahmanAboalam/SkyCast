package com.example.skycast.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skycast.alert.view.Alarm
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.flow.Flow

class FakeRepository: WeatherRepository {

    private val _currentWeather = MutableLiveData<CurrentWetherResponse>()
    val currentWeather: LiveData<CurrentWetherResponse> get() = _currentWeather

    private val _weatherForecast = MutableLiveData<List<WeatherForecastResponse>>()
    val weatherForecast: LiveData<List<WeatherForecastResponse>> get() = _weatherForecast

    var weatherForecastResponse: WeatherForecastResponse? = null
    var currentWeatherResponse: CurrentWetherResponse? = null

    override suspend fun getWeatherForecast(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): WeatherForecastResponse {
        return weatherForecastResponse ?: throw Exception("No weather forecast data available")
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): CurrentWetherResponse {
        return currentWeatherResponse ?: throw Exception("No current weather data available")
    }

    override suspend fun getCurrentWeatherByCity(
        cityName: String,
        lang: String,
        units: String
    ): CurrentWetherResponse {
        return currentWeatherResponse ?: throw Exception("No current weather data available")
    }

    override suspend fun insertWeather(weather: WeatherForecastResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeather(current: CurrentWetherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWeather(): Flow<List<WeatherForecastResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherById(id: Int): WeatherForecastResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherById2(id: Int): WeatherForecastResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeather(weather: WeatherForecastResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWeather(weather: WeatherForecastResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCurrent(): Flow<List<CurrentWetherResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentWeatherById(id: Int): CurrentWetherResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrentWeather(current: CurrentWetherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCurrentWeather(current: CurrentWetherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlarms(): List<Alarm> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }
}
