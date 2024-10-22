package com.example.skycast.db

import android.content.Context
import com.example.skycast.db.tables.WeatherTable

class WeatherLocalDataSourceImpl(private val context: Context): WeatherLocalDataSource {

    private val weatherDao = AppDatabase.getDatabase(context).weatherDao()


    override suspend fun insertWeather(weather: WeatherTable) {
        weatherDao.insert(weather)
    }

    override suspend fun getAllWeather(): List<WeatherTable> {
        return weatherDao.getAllWeather()
    }

    override suspend fun getWeatherById(id: Int): WeatherTable {
        return weatherDao.getWeatherById(id)
    }

}