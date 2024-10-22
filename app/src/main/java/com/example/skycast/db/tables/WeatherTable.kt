package com.example.skycast.db.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherTable (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val location: String,
    val status: String,
    val date: String,
    val temperature: Int,
    val tempMax: Int,
    val tempMin: Int,
    val windSpeed:  Double,
    val humidity: Int,
    val icon: String,
    val longtitude: Double,
    val latitude: Double
)