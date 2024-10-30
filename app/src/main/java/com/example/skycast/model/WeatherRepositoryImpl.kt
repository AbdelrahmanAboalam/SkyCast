package com.example.skycast.model

import com.example.skycast.db.WeatherLocalDataSource
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.network.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource ,
    private val localDataSource: WeatherLocalDataSource
): WeatherRepository {


    override suspend fun getWeatherForecast(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): WeatherForecastResponse = withContext(Dispatchers.IO){
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

    override suspend fun getCurrentWeatherByCity(
        cityName: String,
        lang: String,
        units: String
    ): CurrentWetherResponse = withContext(Dispatchers.IO) {
        val response = remoteDataSource.getCurrentWeatherByCity(cityName, lang, units)
        if (response.isSuccessful){
            response.body() ?: throw Exception("No current weather data available")
        }else{
            throw Exception("Failed to fetch current weather ${response.message()}")
        }
    }

    override suspend fun insertWeather(weather: WeatherForecastResponse) {

        return localDataSource.insertWeather(weather)
    }

    override suspend fun insertWeather(current: CurrentWetherResponse) {
        return localDataSource.insertWeather(current)
    }

    override suspend fun getAllWeather(): List<WeatherForecastResponse> {
        return localDataSource.getAllWeather()
    }

    override suspend fun getWeatherById(id: Int): WeatherForecastResponse? {
        return localDataSource.getWeatherById(id)
    }

    override suspend fun deleteWeather(weather: WeatherForecastResponse) {
        localDataSource.deleteWeather(weather)
    }

    override suspend fun updateWeather(weather: WeatherForecastResponse) {
        localDataSource.updateWeather(weather)
    }

    override suspend fun getAllCurrent(): List<CurrentWetherResponse> {
        return localDataSource.getAllCurrent()
    }

    override suspend fun getCurrentWeatherById(id: Int): CurrentWetherResponse? {
        return localDataSource.getCurrentWeatherById(id)
    }

    override suspend fun deleteCurrentWeather(current: CurrentWetherResponse) {
        localDataSource.deleteCurrentWeather(current)
    }

    override suspend fun updateCurrentWeather(current: CurrentWetherResponse) {
        localDataSource.updateCurrentWeather(current)
    }
}