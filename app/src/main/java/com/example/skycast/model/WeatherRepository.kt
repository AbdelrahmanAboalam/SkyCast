package com.example.skycast.model

import com.example.skycast.db.tables.WeatherTable
import com.example.skycast.model.remote.WetherForeCastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse

interface WeatherRepository {

    suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String , units: String): WetherForeCastResponse
    suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String, units: String): CurrentWetherResponse

    suspend fun insertWeather(weather: WeatherTable)
    suspend fun getAllWeather(): List<WeatherTable>
    suspend fun getWeatherById(id: Int): WeatherTable

}