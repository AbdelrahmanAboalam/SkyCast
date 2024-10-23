package com.example.skycast.db.Converters

import androidx.room.TypeConverter
import com.example.skycast.model.remote.Weather
import com.example.skycast.model.remote.WeatherData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherConverter {

    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>): String {
        val gson = Gson()
        return gson.toJson(weatherList)
    }

    @TypeConverter
    fun toWeatherList(weatherListString: String): List<Weather> {
        val gson = Gson()
        val listType = object : TypeToken<List<WeatherData>>() {}.type
        return gson.fromJson(weatherListString, listType)
    }
}