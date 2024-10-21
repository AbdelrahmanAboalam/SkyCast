package com.example.skycast

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skycast.model.DailyWeatherData
import com.example.skycast.model.HourlyWeatherData
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.model.remote.WetherForeCastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.network.WeatherRemoteDataSource
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var locationManager: LocationGetter
    private lateinit var weatherRepository: WeatherRepositoryImpl

    // UI Components
    private lateinit var searchView: SearchView
    private lateinit var txtLocation: TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtDate: TextView
    private lateinit var imgWeather: ImageView
    private lateinit var txtTemp: TextView
    private lateinit var txtTempRange: TextView
    private lateinit var txtRain: TextView
    private lateinit var txtWind: TextView
    private lateinit var txtHumidity: TextView
    private lateinit var hourRecyclerView: RecyclerView
    private lateinit var forecastRecyclerView: RecyclerView
    private lateinit var btnAddLocation: ExtendedFloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Link UI components with layout
        searchView = view.findViewById(R.id.search_view)
        txtLocation = view.findViewById(R.id.txt_location)
        txtStatus = view.findViewById(R.id.txt_status)
        txtDate = view.findViewById(R.id.txt_date)
        imgWeather = view.findViewById(R.id.img_weather)
        txtTemp = view.findViewById(R.id.txt_temp)
        txtTempRange = view.findViewById(R.id.txt_temp_range)
        txtRain = view.findViewById(R.id.txt_rain)
        txtWind = view.findViewById(R.id.txt_wind)
        txtHumidity = view.findViewById(R.id.txt_humidity)
        hourRecyclerView = view.findViewById(R.id.hour_recycler_view)
        forecastRecyclerView = view.findViewById(R.id.recycler_view)
        btnAddLocation = view.findViewById(R.id.btn_add_location)


        // Setup RecyclerViews
        hourRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        forecastRecyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize location manager and weather repository
        locationManager = LocationGetter(requireContext())
        weatherRepository = WeatherRepositoryImpl(WeatherRemoteDataSource())

        btnAddLocation.setOnClickListener{
            parentFragmentManager.commit {
                replace(R.id.fragment_container, MapFragment()) // Use your fragment container's ID
                addToBackStack(null) // Optional: Add to back stack so you can navigate back
            }

        }

        if (!locationManager.hasLocationPermission()) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 100)
        } else {
            fetchWeather()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchWeather()
        }
    }

    private fun fetchWeather() {
        CoroutineScope(Dispatchers.IO).launch {
            val location = locationManager.getLocation()
            location?.let {
                fetchWeatherData(it.latitude, it.longitude)
            } ?: run {
                // Handle location null (e.g., show error message)
            }
        }
    }

    private fun fetchWeatherData(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lang = "en"
                val units = "metric"
                val currentWeather: CurrentWetherResponse = weatherRepository.getCurrentWeather(lat, lon, lang, units)
                val forecast: WetherForeCastResponse = weatherRepository.getWeatherForecast(lat, lon, lang, units)
                withContext(Dispatchers.Main) {
                    displayWeatherData(forecast, currentWeather)
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun displayWeatherData(forecast: WetherForeCastResponse, current: CurrentWetherResponse) {
        // Update UI components with weather data
        txtLocation.text = current.name
        txtStatus.text = current.weather[0].description
        txtDate.text = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
        txtTemp.text = "${current.main.temp.toInt()}°C"
        txtTempRange.text = "Max: ${current.main.temp_max.toInt()}°C | Min: ${current.main.temp_min.toInt()}°C"
        txtRain.text = "${forecast.list[0].pop?.times(100)}%"
        txtWind.text = "${current.wind.speed} m/s"
        txtHumidity.text = "${current.main.humidity}%"

        val iconUrl = "https://openweathermap.org/img/wn/${current.weather[0].icon}@2x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(imgWeather)


        val hourlyData = forecast.list.take(8).map {
            HourlyWeatherData(time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it.dt * 1000L)),
                temp = it.main.temp.toInt(),
                description = it.weather.joinToString(", ") { it.description })
        }

        val dailyData = forecast.list.drop(8).groupBy { it.dt_txt?.substring(0, 10) }.map { entry ->
            DailyWeatherData(date = entry.key ?: "",
                maxTemp = entry.value.maxOf { it.main.temp_max.toInt() },
                minTemp = entry.value.minOf { it.main.temp_min.toInt() })
        }

        // Set adapters
        hourRecyclerView.adapter = HourlyForecastAdapter(hourlyData)
        forecastRecyclerView.adapter = DailyForecastAdapter(dailyData)
    }
    }

