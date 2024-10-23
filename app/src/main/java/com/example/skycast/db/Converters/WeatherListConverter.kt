package com.example.skycast.db.Converters

import androidx.room.TypeConverter
import com.example.skycast.model.remote.current.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherListConverter {

    private val gson = Gson()

    // Convert List<Weather> to String
    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>?): String? {
        return gson.toJson(weatherList)
    }

    // Convert String to List<Weather>
    @TypeConverter
    fun toWeatherList(weatherListString: String?): List<Weather>? {
        return weatherListString?.let {
            val listType = object : TypeToken<List<Weather>>() {}.type
            gson.fromJson(it, listType)
        }
    }
}