package com.example.skycast.model

import com.example.skycast.model.remote.WetherForeCastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.network.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource): WeatherRepository {


    override suspend fun getWeatherForecast(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): WetherForeCastResponse = withContext(Dispatchers.IO){
        val response = remoteDataSource.getWeatherForecast(lat, lon, lang, units)
        if (response.isSuccessful){
            response.body() ?: throw Exception("No weather forecast data available")
        }else{
            throw Exception("Failed to fetch weather forecast ${response.message()}")
        }

    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): CurrentWetherResponse = withContext(Dispatchers.IO) {
        val response = remoteDataSource.getCurrentWeather(lat, lon, lang, units)
        if (response.isSuccessful){
            response.body() ?: throw Exception("No current weather data available")
        }else{
            throw Exception("Failed to fetch current weather ${response.message()}")
        }
    }

}