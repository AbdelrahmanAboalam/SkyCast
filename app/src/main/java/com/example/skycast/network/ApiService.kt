package com.example.skycast.network

import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface  ApiService {

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String,
        @Query("appid") apiKey: String = "cb075589ca8be42176e5cf5162ab2e69"
    ): Response<WeatherForecastResponse>

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String,
        @Query("appid") apiKey: String = "cb075589ca8be42176e5cf5162ab2e69"
    ): Response<CurrentWetherResponse>

    @GET("weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String,
        @Query("appid") apiKey: String = "cb075589ca8be42176e5cf5162ab2e69"
    ): Response<CurrentWetherResponse>
}