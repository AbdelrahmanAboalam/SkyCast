package com.example.skycast.db

import android.content.Context
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherLocalDataSourceImpl(private val context: Context, private val weatherDao: WeatherDao = AppDatabase.getDatabase(context).weatherDao()): WeatherLocalDataSource {



    override suspend fun insertWeather(weather: WeatherForecastResponse) {
        withContext(Dispatchers.IO) {
            weatherDao.insertWeatherForecastResponse(weather)
        }
    }

    override suspend fun insertWeather(current: CurrentWetherResponse) {
        weatherDao.insertCurrentWeatherResponse(current)
    }

    override fun getAllWeather(): Flow<List<WeatherForecastResponse>> {
        return weatherDao.getAllWeatherForecasts()
    }

    override suspend fun getWeatherById(id: Int): WeatherForecastResponse {
        return weatherDao.getWeatherForecastById(id)
    }

    override suspend fun getWeatherById2(id: Int): WeatherForecastResponse {
        return weatherDao.getWeatherForecastById2(id)
    }

    override suspend fun deleteWeather(weather: WeatherForecastResponse) {
        weatherDao.deleteWeatherForecastResponse(weather)
    }

    override suspend fun updateWeather(weather: WeatherForecastResponse) {
        weatherDao.updateWeatherForecastResponse(weather)
    }

    override  fun getAllCurrent(): Flow<List<CurrentWetherResponse>> {
        return weatherDao.getAllCurrentWeather()
    }

    override suspend fun getCurrentWeatherById(id: Int): CurrentWetherResponse {
        return weatherDao.getCurrentWeatherById(id)
    }

    override suspend fun deleteCurrentWeather(current: CurrentWetherResponse) {
        weatherDao.deleteCurrentWeatherResponse(current)
    }

    override suspend fun updateCurrentWeather(current: CurrentWetherResponse) {
        weatherDao.updateCurrentWeatherResponse(current)
    }


}