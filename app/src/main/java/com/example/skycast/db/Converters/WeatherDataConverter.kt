package com.example.skycast.db.Converters

import androidx.room.TypeConverter
import com.example.skycast.model.remote.Clouds
import com.example.skycast.model.remote.Weather
import com.example.skycast.model.remote.WeatherData
import com.example.skycast.model.remote.Wind
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherDataConverter {
    @TypeConverter
    fun fromWeatherDataList(value: List<WeatherData>): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWeatherDataList(value: String): List<WeatherData> {
        val gson = Gson()
        val listType = object : TypeToken<List<WeatherData>>() {}.type
        return gson.fromJson(value, listType)
    }
}







