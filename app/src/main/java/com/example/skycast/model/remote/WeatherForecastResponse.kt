package com.example.skycast.model.remote

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.skycast.db.Converters.WeatherDataConverter
import com.example.skycast.db.Converters.WeatherConverter

@Entity(tableName = "weather_forecast_table")
data class WeatherForecastResponse(
    @PrimaryKey(autoGenerate = true)
    var idKey: Int = 0,

    @Embedded
    val city: City,

    val cnt: Int,
    val cod: String,

    @TypeConverters(WeatherDataConverter::class)
    val list: List<WeatherData> = emptyList(),

    val message: Int
) {
    // Required empty constructor for Room
    constructor() : this(city = City(), cnt = 0, cod = "", list = emptyList(), message = 0)
}

data class City(
    @Embedded
    val coord: Coord = Coord(),
    val country: String = "",
    val id: Int = 0,
    @PrimaryKey
    val name: String = "",
    val population: Int = 0,
    val sunrise: Int = 0,
    val sunset: Int = 0,
    val timezone: Int = 0
)

data class Coord(
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

data class WeatherData(
    @Embedded
    val clouds: Clouds = Clouds(),
    val dt: Int = 0,
    val dt_txt: String = "",
    @Embedded
    val main: Main = Main(),
    val pop: Double = 0.0,
    @Embedded
    val rain: Rain = Rain(),
    @Embedded
    val sys: Sys = Sys(),
    val visibility: Int = 0,
    @TypeConverters(WeatherConverter::class)
    @Embedded
    val weather: List<Weather> = emptyList(),
    @Embedded
    val wind: Wind = Wind()
)

data class Weather(
    val description: String = "",
    val icon: String = "",
    val id: Int = 0,
    val main: String = ""
)

data class Rain(
    val `3h`: Double = 0.0
)

data class Wind(
    val deg: Int = 0,
    val gust: Double = 0.0,
    val speed: Double = 0.0
)

data class Sys(
    val pod: String = ""
)

data class Clouds(
    val all: Int = 0
)

data class Main(
    val feels_like: Double = 0.0,
    val grnd_level: Int = 0,
    val humidity: Int = 0,
    val pressure: Int = 0,
    val sea_level: Int = 0,
    val temp: Double = 0.0,
    val temp_kf: Double = 0.0,
    val temp_max: Double = 0.0,
    val temp_min: Double = 0.0
)
