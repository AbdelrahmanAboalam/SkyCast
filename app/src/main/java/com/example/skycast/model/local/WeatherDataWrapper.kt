package com.example.skycast.model.local

import com.example.skycast.model.remote.WeatherData

sealed class WeatherDataWrapper {

    data class Hourly(val weatherData: WeatherData) : WeatherDataWrapper()
    data class Daily(val date: String, val minTemp: Double, val maxTemp: Double) : WeatherDataWrapper()
}