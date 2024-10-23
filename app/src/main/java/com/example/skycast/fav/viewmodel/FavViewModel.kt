package com.example.skycast.fav.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import kotlinx.coroutines.launch

class FavViewModel(private val repository: WeatherRepository, private val context: Context) : ViewModel() {

    private val _currentWeatherList = MutableLiveData<List<CurrentWetherResponse>>()
    val currentWeatherList: LiveData<List<CurrentWetherResponse>> get() = _currentWeatherList

    private val _currentWeather = MutableLiveData<List<WeatherForecastResponse>>()
    val currentWeather: LiveData<List<WeatherForecastResponse>> get() = _currentWeather

    private val _currentGetWeather = MutableLiveData<CurrentWetherResponse?>()
    val currentGetWeather: LiveData<CurrentWetherResponse?> get() = _currentGetWeather

    private val _currentGetForCastWeather = MutableLiveData<WeatherForecastResponse?>()
    val currentGetForCastWeather: LiveData<WeatherForecastResponse?> get() = _currentGetForCastWeather


    fun fetchAllCurrentWeather() {
        viewModelScope.launch {
            if (isNetworkAvailable(context)) {
                // Fetch weather data from remote source and update local database
                val remoteWeatherData = repository.getAllCurrent() // Assuming this fetches from remote

                remoteWeatherData?.let {
                    // Update the local database with the fetched data
                    it.forEach { weather ->
                        fetchAndUpdateWeather(weather)
                    }
                }
            }
            // Now fetch from the local database
            _currentWeatherList.value = repository.getAllCurrent()
        }
    }

    fun deleteCurrentWeather(weather: CurrentWetherResponse) {
        viewModelScope.launch {
            repository.deleteCurrentWeather(weather)
            fetchAllCurrentWeather()
        }
    }

    suspend fun fetchAndUpdateWeather(weatherData: CurrentWetherResponse) {
        if (isNetworkAvailable(context)) {
            // Fetch the latest current weather data
            val currentWeatherResponse = repository.getCurrentWeather(
                weatherData.coord.lat,
                weatherData.coord.lon,
                "en",
                "metric"
            )
            Log.d("FavViewModel", "Updating current weather: $currentWeatherResponse")
            if (currentWeatherResponse != null) {
                // Update your local database with the fetched data
                repository.updateCurrentWeather(currentWeatherResponse)
            }

            // Fetch the weather forecast as well, if needed
            val forecastResponse = repository.getWeatherForecast(
                weatherData.coord.lat,
                weatherData.coord.lon,
                "en",
                "metric"
            )
            if (forecastResponse != null) {
                // Update local database with forecast data if needed
                repository.updateWeather(forecastResponse)
            }
        } else {
            // If no network, just fetch from local database
        }
    }

    fun getCurrentWeatherById (idKey: Int) = viewModelScope.launch {
        val currentWeather = repository.getWeatherById(idKey)
        val currentWeatherList = repository.getCurrentWeatherById(idKey)

        _currentGetWeather.value =currentWeatherList
        _currentGetForCastWeather.value = currentWeather

    }


    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            // For older devices
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}
