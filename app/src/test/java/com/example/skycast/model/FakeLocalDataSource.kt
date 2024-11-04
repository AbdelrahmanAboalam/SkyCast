package com.example.skycast.model

import com.example.skycast.alert.view.Alarm
import com.example.skycast.db.WeatherLocalDataSource
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.flow.Flow

class FakeLocalDataSource: WeatherLocalDataSource {
    override suspend fun insertWeather(weather: WeatherForecastResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeather(current: CurrentWetherResponse) {
        TODO("Not yet implemented")
    }

    override fun getAllWeather(): Flow<List<WeatherForecastResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherById(id: Int): WeatherForecastResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherById2(id: Int): WeatherForecastResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeather(weather: WeatherForecastResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWeather(weather: WeatherForecastResponse) {
        TODO("Not yet implemented")
    }

    override fun getAllCurrent(): Flow<List<CurrentWetherResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentWeatherById(id: Int): CurrentWetherResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrentWeather(current: CurrentWetherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCurrentWeather(current: CurrentWetherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlarms(): List<Alarm> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }
}

