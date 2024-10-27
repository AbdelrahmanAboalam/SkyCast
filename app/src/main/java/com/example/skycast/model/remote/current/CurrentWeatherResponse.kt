package com.example.skycast.model.remote.current

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.skycast.db.Converters.WeatherConverter
import com.example.skycast.db.Converters.WeatherListConverter
import com.example.skycast.model.remote.WeatherForecastResponse

@Entity(tableName = "current_weather_table")
data class CurrentWetherResponse(
    @PrimaryKey(autoGenerate = true)
    var idKey: Int = 0,

    val base: String = "",

    @Embedded
    val clouds: Clouds = Clouds(),

    val cod: Int = 200,

    @Embedded
    val coord: Coord = Coord(),

    val dt: Int = 0,
    @ColumnInfo(name = "current_weather_id")
    val id: Int = 0,

    @Embedded
    val main: Main = Main(),

    val name: String = "",

    @Embedded
    val sys: Sys = Sys(),

    val timezone: Int = 0,

    val visibility: Int = 0,

    @TypeConverters(WeatherListConverter::class)
    val weather: List<Weather> = emptyList(),

    @Embedded
    val wind: Wind = Wind()
)

data class Sys(
    val country: String = "",
    @ColumnInfo(name = "sys_id")
    val id: Int = 0,
    val sunrise: Int = 0,
    val sunset: Int = 0,
    val type: Int = 0
)

data class Main(
    val feels_like: Double = 0.0,
    val grnd_level: Int = 0,
    val humidity: Int = 0,
    val pressure: Int = 0,
    val sea_level: Int = 0,
    val temp: Double = 0.0,
    val temp_max: Double = 0.0,
    val temp_min: Double = 0.0
)

data class Weather(
    val description: String = "",
    val icon: String = "",
    @ColumnInfo(name = "weather_id")
    val id: Int = 0,
    val main: String = ""
)

data class Wind(
    val deg: Int = 0,
    val gust: Double = 0.0,
    val speed: Double = 0.0
)

data class Clouds(
    val all: Int = 0
)

data class Coord(
    val lat: Double = 0.0,
    val lon: Double = 0.0
)
