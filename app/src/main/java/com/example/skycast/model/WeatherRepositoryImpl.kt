package com.example.skycast.model

import com.example.skycast.alert.view.Alarm
import com.example.skycast.db.WeatherLocalDataSource
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.network.WeatherRemoteDataSource
import com.example.skycast.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
): WeatherRepository {


    override suspend fun getWeatherForecast(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): Flow<WeatherForecastResponse> = flow{
        val response = remoteDataSource.getWeatherForecast(lat, lon, lang, units)
        if (response.isSuccessful){
            emit(response.body() ?: WeatherForecastResponse())
        }else{
            throw Exception("Failed to fetch weather forecast")
        }

    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): Flow<CurrentWetherResponse> = flow {
        val response = remoteDataSource.getCurrentWeather(lat, lon, lang, units)
        if (response.isSuccessful){
            emit( response.body() ?: CurrentWetherResponse())
        }else{
            throw Exception("Failed to fetch current weather ${response.message()}")
        }
    }

    override suspend fun getCurrentWeatherByCity(
        cityName: String,
        lang: String,
        units: String
    ): Flow<CurrentWetherResponse> = flow{
        val response = remoteDataSource.getCurrentWeatherByCity(cityName, lang, units)
        if (response.isSuccessful){
            emit( response.body() ?: CurrentWetherResponse())
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

    override suspend fun getAllWeather(): Flow<List<WeatherForecastResponse>> {
        return localDataSource.getAllWeather()
    }

    override suspend fun getWeatherById(id: Int): WeatherForecastResponse? {
        return localDataSource.getWeatherById(id)
    }

    override suspend fun getWeatherById2(id: Int): WeatherForecastResponse {
        return localDataSource.getWeatherById2(id)
    }

    override suspend fun deleteWeather(weather: WeatherForecastResponse) {
        localDataSource.deleteWeather(weather)
    }

    override suspend fun updateWeather(weather: WeatherForecastResponse) {
        localDataSource.updateWeather(weather)
    }

    override suspend fun getAllCurrent(): Flow<List<CurrentWetherResponse>> {
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

    override suspend fun insertAlarm(alarm: Alarm) {
        localDataSource.insertAlarm(alarm)
    }

    override suspend fun getAllAlarms(): List<Alarm> {
        return localDataSource.getAllAlarms()
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        localDataSource.deleteAlarm(alarm)
    }
}