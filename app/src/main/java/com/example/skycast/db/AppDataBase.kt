package com.example.skycast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skycast.db.Converters.CloudsConverter
import com.example.skycast.db.Converters.WeatherDataConverter
import com.example.skycast.db.Converters.WeatherConverter
import com.example.skycast.db.Converters.WeatherListConverter
import com.example.skycast.db.Converters.WindConverter
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.City
import com.example.skycast.model.remote.WeatherData
import com.example.skycast.model.remote.current.CurrentWetherResponse
@Database(
    entities = [WeatherForecastResponse::class, CurrentWetherResponse::class],
    version = 1
)
@TypeConverters(WeatherDataConverter::class, WeatherConverter::class,WeatherListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
