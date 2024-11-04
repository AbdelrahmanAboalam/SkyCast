package com.example.skycast.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherForecastResponse(forecastResponse: WeatherForecastResponse)

    // Get all WeatherForecastResponse records
    @Query("SELECT * FROM weather_forecast_table")
     fun getAllWeatherForecasts(): Flow<List<WeatherForecastResponse>>

    // Get a specific WeatherForecastResponse by idKey
    @Query("SELECT * FROM weather_forecast_table WHERE idKey = :idKey")
    suspend fun getWeatherForecastById(idKey: Int): WeatherForecastResponse

    // Get a specific WeatherForecastResponse by idKey
    @Query("SELECT * FROM weather_forecast_table WHERE idKey = :idKey")
    suspend fun getWeatherForecastById2(idKey: Int): WeatherForecastResponse

    // Update a WeatherForecastResponse record
    @Update( onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWeatherForecastResponse(forecastResponse: WeatherForecastResponse)

    // Delete a WeatherForecastResponse record
    @Delete
    suspend fun deleteWeatherForecastResponse(forecastResponse: WeatherForecastResponse)

    // Clear the weather_forecast_table
    @Query("DELETE FROM weather_forecast_table")
    suspend fun clearWeatherForecastTable()

    /////////////////////////////////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeatherResponse(currentWeatherResponse: CurrentWetherResponse)

    @Query("SELECT * FROM current_weather_table")
    fun getAllCurrentWeather(): Flow<List<CurrentWetherResponse>>

    @Query("SELECT * FROM current_weather_table WHERE idKey = :idKey")
    suspend fun getCurrentWeatherById(idKey: Int): CurrentWetherResponse


    @Delete
    suspend fun deleteCurrentWeatherResponse(currentWeatherResponse: CurrentWetherResponse)

    @Update( onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCurrentWeatherResponse(currentWeatherResponse: CurrentWetherResponse)
}