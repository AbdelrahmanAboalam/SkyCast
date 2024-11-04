package com.example.skycast.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.model.remote.City
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class WeatherDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        weatherDao = database.weatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun WeatherDao_insertWeatherForecastResponse_retrievesById() = runTest {
        val weatherForecast = WeatherForecastResponse(
            idKey = 1,
            city = City(name = "Test City", id = 1),
            cnt = 1,
            cod = "200",
            list = emptyList(),
            message = 0
        )

        weatherDao.insertWeatherForecastResponse(weatherForecast)

        val result = weatherDao.getWeatherForecastById(weatherForecast.idKey)

        assertThat(result, notNullValue())
        assertThat(result.idKey, `is`(weatherForecast.idKey))
    }

    @Test
    fun WeatherDao_updateWeatherForecastResponse_retrievesUpdatedById() = runTest {
        val initialForecastResponse = WeatherForecastResponse(
            idKey = 2,
            city = City(name = "Test City", id = 1),
            cnt = 1,
            cod = "200",
            list = emptyList(),
            message = 0
        )
        weatherDao.insertWeatherForecastResponse(initialForecastResponse)

        val updatedForecastResponse = WeatherForecastResponse(
            idKey = initialForecastResponse.idKey,
            city = City(name = "Updated City", id = 1),
            cnt = 2,
            cod = "200",
            list = emptyList(),
            message = 0
        )

        weatherDao.updateWeatherForecastResponse(updatedForecastResponse)

        val result = weatherDao.getWeatherForecastById(initialForecastResponse.idKey)

        assertThat(result, notNullValue())
        assertThat(result.idKey, `is`(updatedForecastResponse.idKey))
    }

    @Test
    fun WeatherDao_insertCurrentWeatherResponse_retrievesById() = runBlockingTest {
        val currentWeatherResponse = CurrentWetherResponse(
            idKey = 1,
        )

        weatherDao.insertCurrentWeatherResponse(currentWeatherResponse)

        val result = weatherDao.getCurrentWeatherById(currentWeatherResponse.idKey)

        assertThat(result, notNullValue())
        assertThat(result.idKey, `is`(currentWeatherResponse.idKey))
    }

    @Test
    fun WeatherDao_updateCurrentWeatherResponse_retrievesUpdatedById() = runBlockingTest {
        val initialCurrentWeatherResponse = CurrentWetherResponse(
            idKey = 1,
        )
        weatherDao.insertCurrentWeatherResponse(initialCurrentWeatherResponse)

        val updatedCurrentWeatherResponse = CurrentWetherResponse(
            idKey = initialCurrentWeatherResponse.idKey,
        )

        weatherDao.updateCurrentWeatherResponse(updatedCurrentWeatherResponse)

        val result = weatherDao.getCurrentWeatherById(initialCurrentWeatherResponse.idKey)

        assertThat(result, notNullValue())
        assertThat(result.idKey, `is`(updatedCurrentWeatherResponse.idKey))
    }
}
