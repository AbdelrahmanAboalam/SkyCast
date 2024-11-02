package com.example.skycast.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.model.remote.current.Clouds
import com.example.skycast.model.remote.City
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class WeatherLocalDataSourceImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var weatherDao: WeatherDao
    private lateinit var weatherLocalDataSource: WeatherLocalDataSourceImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        weatherDao = database.weatherDao()

        weatherLocalDataSource = WeatherLocalDataSourceImpl(context, weatherDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertWeather_retrievesWeather() = runTest {
        val weather = WeatherForecastResponse(
            idKey = 1,
            city = City(name = "Test City", id = 1),
            cnt = 1,
            cod = "200",
            list = emptyList(),
            message = 0
        )

        weatherLocalDataSource.insertWeather(weather)

        val result = weatherLocalDataSource.getWeatherById(1)

        assertThat(result.idKey, `is`(1))
        assertThat(result.city.name, `is`("Test City"))
    }

    @Test
    fun insertCurrentWeather_retrievesCurrentWeather() = runTest {
        val currentWeather = CurrentWetherResponse(
            idKey = 2,
            base = "base",
            clouds = Clouds(all = 1)
        )

        weatherLocalDataSource.insertWeather(currentWeather)

        val result = weatherLocalDataSource.getCurrentWeatherById(2)

        assertThat(result.idKey, `is`(2))
        assertThat(result.base, `is`("base"))
        assertThat(result.clouds.all, `is`(1))
    }

    @Test
    fun deleteWeather_checkIfDeleted() = runTest {
        val weather = WeatherForecastResponse(
            idKey = 1,
            city = City(name = "Test City", id = 1),
            cnt = 1,
            cod = "200",
            list = emptyList(),
            message = 0
        )
        weatherLocalDataSource.insertWeather(weather)

        weatherLocalDataSource.deleteWeather(weather)

        val allWeather = weatherLocalDataSource.getAllWeather().first()
        assertThat(allWeather.contains(weather), `is`(false))
    }

    @Test
    fun getAllWeather_retrievesAllWeather() = runTest {
        val weather1 = WeatherForecastResponse(idKey = 1, city = City(name = "City 1", id = 1), cnt = 1, cod = "200", list = emptyList(), message = 0)
        val weather2 = WeatherForecastResponse(idKey = 2, city = City(name = "City 2", id = 2), cnt = 1, cod = "200", list = emptyList(), message = 0)

        weatherLocalDataSource.insertWeather(weather1)
        weatherLocalDataSource.insertWeather(weather2)

        val result = weatherLocalDataSource.getAllWeather().first()

        assertThat(result.size, `is`(2))
        assertThat(result.map { it.idKey }, `is`(listOf(1, 2)))
    }
}
