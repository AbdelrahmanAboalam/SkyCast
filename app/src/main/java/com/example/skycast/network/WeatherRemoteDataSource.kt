package com.example.skycast.network

import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import retrofit2.Response

class WeatherRemoteDataSource {
    private val apiService = RetrofitHelper.getApiService()

    suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String, unit: String): Response<WeatherForecastResponse> {
        return apiService.getWeatherForecast(lat, lon, lang = lang, units = unit)
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String, unit: String): Response<CurrentWetherResponse> {
        return apiService.getCurrentWeather(lat, lon, lang = lang, units = unit)
    }

}