package com.example.skycast.network

import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import retrofit2.Response

open class WeatherRemoteDataSourceImpl: WeatherRemoteDataSource {
    private val apiService = RetrofitHelper.getApiService()

    override suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String, unit: String): Response<WeatherForecastResponse> {
        return apiService.getWeatherForecast(lat, lon, lang = lang, units = unit)
    }

    override suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String, unit: String): Response<CurrentWetherResponse> {
        return apiService.getCurrentWeather(lat, lon, lang = lang, units = unit)
    }

    override suspend fun getCurrentWeatherByCity(cityName: String, lang: String, unit: String): Response<CurrentWetherResponse> {
        return apiService.getCurrentWeatherByCity(cityName, lang = lang, units = unit)
    }

}