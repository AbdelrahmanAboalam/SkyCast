package com.example.skycast.db

import com.example.skycast.db.tables.WeatherTable

interface WeatherLocalDataSource {
    suspend fun insertWeather(weather: WeatherTable)
    suspend fun getAllWeather(): List<WeatherTable>
    suspend fun getWeatherById(id: Int): WeatherTable

}