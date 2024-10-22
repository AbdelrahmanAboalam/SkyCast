package com.example.skycast.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.skycast.db.tables.WeatherTable

@Dao
interface WeatherDao {
    @Insert
    suspend fun insert(weather: WeatherTable)

    @Query("SELECT * FROM weather_table ORDER BY id DESC")
    suspend fun getAllWeather(): List<WeatherTable>

    @Query("SELECT * FROM weather_table WHERE id = :id")
    suspend fun getWeatherById(id: Int): WeatherTable

}