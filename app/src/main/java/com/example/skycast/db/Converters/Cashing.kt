package com.example.skycast.db

import android.content.Context
import com.google.gson.Gson
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse

class Cashing(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("WeatherCache", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Initialize cache with default data if needed
    fun initializeCache() {
        if (sharedPreferences.getBoolean("first_cache", true)) {
            // Cache default or empty data for the first launch
            cacheCurrentWeather(null) // Use null or replace with your default object
            cacheWeatherForecast(null) // Use null or replace with your default object
            setFirstCache(false)
        }
    }

    private fun setFirstCache(isFirstCache: Boolean) {
        sharedPreferences.edit().putBoolean("first_cache", isFirstCache).apply()
    }

    // Cache current weather
    fun cacheCurrentWeather(data: CurrentWetherResponse?) {
        val json = gson.toJson(data)
        sharedPreferences.edit().putString("CurrentWeather", json).apply()
    }

    // Retrieve cached current weather
    fun getCachedCurrentWeather(): CurrentWetherResponse? {
        val json = sharedPreferences.getString("CurrentWeather", null)
        return if (json != null) {
            gson.fromJson(json, CurrentWetherResponse::class.java)
        } else {
            null
        }
    }

    // Cache weather forecast
    fun cacheWeatherForecast(data: WeatherForecastResponse?) {
        val json = gson.toJson(data)
        sharedPreferences.edit().putString("WeatherForecast", json).apply()
    }

    // Retrieve cached weather forecast
    fun getCachedWeatherForecast(): WeatherForecastResponse? {
        val json = sharedPreferences.getString("WeatherForecast", null)
        return if (json != null) {
            gson.fromJson(json, WeatherForecastResponse::class.java)
        } else {
            null
        }
    }
}
