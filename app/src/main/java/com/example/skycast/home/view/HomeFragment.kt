package com.example.skycast.home.view

import SharedWeatherViewModel
import com.example.skycast.home.viewmodel.HomeViewModel
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skycast.LocationGetter
import com.example.skycast.MapFragment
import com.example.skycast.R
import com.example.skycast.db.WeatherLocalDataSourceImpl
import com.example.skycast.home.viewmodel.HomeViewModelFactory
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
import kotlin.properties.Delegates

class HomeFragment : Fragment() {

    private lateinit var locationManager: LocationGetter
    private lateinit var weatherRepository: WeatherRepositoryImpl
    private lateinit var viewModel: HomeViewModel
    private lateinit var sharedWeatherViewModel: SharedWeatherViewModel

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

    companion object {
        var isCurrentLocation = true
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedWeatherViewModel = ViewModelProvider(requireActivity()).get(SharedWeatherViewModel::class.java)
        sharedWeatherViewModel.currentWeather.observe(this, Observer { currentWeather ->
            isCurrentLocation = false
            currentWeather?.let { updateCurrentWeather(it) }

        })
        sharedWeatherViewModel.weatherForecast.observe(this, Observer { forecastWeather ->
            isCurrentLocation = false
            forecastWeather?.let { updateForecastWeather(it) }
        })

    }

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
        weatherRepository = WeatherRepositoryImpl(WeatherRemoteDataSource(), WeatherLocalDataSourceImpl(requireContext()))

        // Initialize ViewModel
        val factory = HomeViewModelFactory(weatherRepository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        // Observe LiveData from ViewModel
        viewModel.currentWeather.observe(viewLifecycleOwner, Observer { currentWeather ->
            currentWeather?.let {
                updateCurrentWeather(it)
            }
        })

        viewModel.weatherForecast.observe(viewLifecycleOwner, Observer { forecast ->
            forecast?.let {
                updateForecastWeather(it)
            }
        })

        btnAddLocation.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, MapFragment())
                addToBackStack(null) // Optional: Add to back stack
            }
        }

        // Check for location permissions and fetch weather
        if (!locationManager.hasLocationPermission()) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 100)
        } else if(isCurrentLocation){
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
        CoroutineScope(Dispatchers.Main).launch {
            val location = withContext(Dispatchers.IO) {
                locationManager.getLocation() // Call the suspend function
            }
            location ?. let {
                viewModel.fetchWeather(it.latitude, it.longitude, "en", "metric") // Add language and units
            } ?: run {
                // Handle location null (e.g., show error message)
            }
        }
    }

    private fun updateCurrentWeather(current: CurrentWetherResponse) {
        // Update UI components with current weather data
        txtLocation.text = current.name
        txtStatus.text = current.weather[0].description
        txtDate.text = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
        txtTemp.text = "${current.main.temp.toInt()}°C"
        txtTempRange.text = "Max: ${current.main.temp_max.toInt()}°C | Min: ${current.main.temp_min.toInt()}°C"
        txtWind.text = "${current.wind.speed} m/s"
        txtHumidity.text = "${current.main.humidity}%"

        val iconUrl = "https://openweathermap.org/img/wn/${current.weather[0].icon}@2x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(imgWeather)
    }

    private fun updateForecastWeather(forecast: WetherForeCastResponse) {
        // Update UI components with forecast data
        txtRain.text = "${forecast.list[0].pop?.times(100)}%"

        val hourlyData = forecast.list.take(8).map {
            HourlyWeatherData(
                time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it.dt * 1000L)),
                temp = it.main.temp.toInt(),
                description = it.weather.joinToString(", ") { it.description }
            )
        }

        val dailyData = forecast.list.drop(8).groupBy { it.dt_txt?.substring(0, 10) }.map { entry ->
            DailyWeatherData(
                date = entry.key ?: "",
                maxTemp = entry.value.maxOf { it.main.temp_max.toInt() },
                minTemp = entry.value.minOf { it.main.temp_min.toInt() }
            )
        }

        // Set adapters
        hourRecyclerView.adapter = HourlyForecastAdapter(hourlyData)
        forecastRecyclerView.adapter = DailyForecastAdapter(dailyData)
    }
}
