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







class CloudsConverter {
    @TypeConverter
    fun fromClouds(clouds: Clouds?): String? {
        val gson = Gson()
        return clouds?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toClouds(value: String?): Clouds? {
        val gson = Gson()
        return value?.let {
            val type = object : TypeToken<Clouds>() {}.type
            gson.fromJson(it, type)
        }
    }
}

class WindConverter {

    @TypeConverter
    fun fromWind(wind: Wind): String {
        return "${wind.deg},${wind.gust},${wind.speed}"
    }

    @TypeConverter
    fun toWind(data: String): Wind {
        val parts = data.split(",")
        return Wind(
            deg = parts[0].toInt(),
            gust = parts[1].toDouble(),
            speed = parts[2].toDouble()
        )
    }
}