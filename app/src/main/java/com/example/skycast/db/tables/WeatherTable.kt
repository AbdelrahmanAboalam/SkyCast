//package com.example.skycast.db.tables
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//@Entity(tableName = "weather_table")
//data class WeatherTable (
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val location: String,
//    val status: String,
//    val date: String,
//    val temperature: Int,
//    val tempMax: Int,
//    val tempMin: Int,
//    val windSpeed: Double,
//    val humidity: Int,
//    val icon: String,
//    val longitude: Double,
//    val latitude: Double,
//
//    // Additional fields for CurrentWeatherResponse and WeatherData
//    val base: String? = null,
//    val cod: Int? = null,
//    val visibility: Int? = null,
//    val pop: Double? = null,
//    val rain3h: Double? = null,
//    val sysPod: String? = null,
//    val sysCountry: String? = null,
//    val coordLat: Double? = null,
//    val coordLon: Double? = null
//)
