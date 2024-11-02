package com.example.skycast.network

import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String, units: String): Response<WeatherForecastResponse>
    suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String, units: String): Response<CurrentWetherResponse>
    suspend fun getCurrentWeatherByCity(cityName: String, lang: String, units: String): Response<CurrentWetherResponse>


}