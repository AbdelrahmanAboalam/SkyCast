package com.example.skycast.home.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.ViewModelRule
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.Coord
import com.example.skycast.model.remote.current.CurrentWetherResponse
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var weatherRepository: FakeRepository
    private lateinit var context: Context

    @get:Rule
    val viewModelRule = ViewModelRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        context = ApplicationProvider.getApplicationContext()
        weatherRepository = FakeRepository()
        homeViewModel = HomeViewModel(weatherRepository, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchWeather_updatesCurrentWeatherAndWeatherForecast() = runTest {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060
        val mockCurrentWeather = CurrentWetherResponse()
        val mockForecast = WeatherForecastResponse()

        weatherRepository.currentWeatherResponse = mockCurrentWeather
        weatherRepository.weatherForecastResponse = mockForecast

        // When
        homeViewModel.fetchWeather(latitude, longitude)
        advanceUntilIdle() // Ensure all coroutines have completed

        // Assert
        val currentWeather = homeViewModel.currentWeather.getOrAwaitValue()
        val forecast = homeViewModel.weatherForecast.getOrAwaitValue()
        assertThat(currentWeather, equalTo(mockCurrentWeather))
        assertThat(forecast, equalTo(mockForecast))
    }

    @Test
    fun fetchWeatherByCity_updatesCurrentWeatherByCity() = runTest {
        // Given
        val cityName = "New York"
        val mockCurrentWeatherByCity = CurrentWetherResponse(coord = Coord(lat = 40.7128, lon = -74.0060))

        // Set the mock response for the city fetch
        weatherRepository.currentWeatherResponse = mockCurrentWeatherByCity

        // Call the method to fetch weather by city
        homeViewModel.fetchWeatherByCity(cityName)
        advanceUntilIdle() // Ensure all coroutines have completed

        // Assert
        val currentWeatherByCity = homeViewModel.currentWeatherByCity.getOrAwaitValue()
        assertThat(currentWeatherByCity, equalTo(mockCurrentWeatherByCity))
    }
}
