package com.example.skycast.model


data class DailyWeatherData(val date: String, val maxTemp: Int, val minTemp: Int,val icon: String)

data class HourlyWeatherData(val time: String, val temp: Int, val description: String, val icon: String)
