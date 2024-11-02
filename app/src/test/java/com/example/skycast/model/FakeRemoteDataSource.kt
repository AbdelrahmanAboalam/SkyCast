package com.example.skycast.model

import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.network.WeatherRemoteDataSource
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

class FakeRemoteDataSource : WeatherRemoteDataSource {

    var weatherForecastResponse: WeatherForecastResponse? = null
    var currentWeatherResponse: CurrentWetherResponse? = null
    var shouldReturnError: Boolean = false

    override suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String, units: String): Response<WeatherForecastResponse> {
        return if (shouldReturnError) {
            Response.error(
                500,
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    "{\"error\":\"Failed to fetch weather forecast\"}"
                )
            )
        } else {
            Response.success(weatherForecastResponse)
        }
    }

    override suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String, units: String): Response<CurrentWetherResponse> {
        return if (shouldReturnError) {
            Response.error(
                500,
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    "{\"error\":\"Failed to fetch current weather\"}"
                )
            )
        } else {
            Response.success(currentWeatherResponse)
        }
    }

    override suspend fun getCurrentWeatherByCity(cityName: String, lang: String, units: String): Response<CurrentWetherResponse> {
        return Response.success(currentWeatherResponse)
    }
}