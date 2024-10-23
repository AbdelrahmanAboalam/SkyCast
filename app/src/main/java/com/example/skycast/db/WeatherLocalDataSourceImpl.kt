package com.example.skycast.db

import android.content.Context
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse

class WeatherLocalDataSourceImpl(private val context: Context): WeatherLocalDataSource {

    private val weatherDao = AppDatabase.getDatabase(context).weatherDao()


    override suspend fun insertWeather(weather: WeatherForecastResponse) {
        weatherDao.insertWeatherForecastResponse(weather)
    }

    override suspend fun insertWeather(current: CurrentWetherResponse) {
        weatherDao.insertCurrentWeatherResponse(current)
    }

    override suspend fun getAllWeather(): List<WeatherForecastResponse> {
        return weatherDao.getAllWeatherForecasts()
    }

    override suspend fun getWeatherById(id: Int): WeatherForecastResponse? {
        return weatherDao.getWeatherForecastById(id)
    }

    override suspend fun deleteWeather(weather: WeatherForecastResponse) {
        weatherDao.deleteWeatherForecastResponse(weather)
    }

    override suspend fun updateWeather(weather: WeatherForecastResponse) {
        weatherDao.updateWeatherForecastResponse(weather)
    }

    override suspend fun getAllCurrent(): List<CurrentWetherResponse> {
        return weatherDao.getAllCurrentWeather()
    }

    override suspend fun getCurrentWeatherById(id: Int): CurrentWetherResponse? {
        return weatherDao.getCurrentWeatherById(id)
    }

    override suspend fun deleteCurrentWeather(current: CurrentWetherResponse) {
        weatherDao.deleteCurrentWeatherResponse(current)
    }

    override suspend fun updateCurrentWeather(current: CurrentWetherResponse) {
        weatherDao.updateCurrentWeatherResponse(current)
    }


}