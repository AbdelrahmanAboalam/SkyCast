package com.example.skycast.db

import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun insertWeather(weather: WeatherForecastResponse) // Changed parameter type to WeatherForecastResponse
    fun getAllWeather(): Flow<List<WeatherForecastResponse>> // Changed return type to List<WeatherForecastResponse>
    suspend fun getWeatherById(id: Int): WeatherForecastResponse
    suspend fun getWeatherById2(id: Int): WeatherForecastResponse
    suspend fun deleteWeather(weather: WeatherForecastResponse)
    suspend fun updateWeather(weather: WeatherForecastResponse)


    suspend fun insertWeather(current : CurrentWetherResponse) // Changed parameter type to WeatherForecastResponse
    fun getAllCurrent(): Flow<List<CurrentWetherResponse>> // Changed return type to List<WeatherForecastResponse>
    suspend fun getCurrentWeatherById(id: Int): CurrentWetherResponse
    suspend fun deleteCurrentWeather(current : CurrentWetherResponse)
    suspend fun updateCurrentWeather(current : CurrentWetherResponse)



}