package com.example.skycast.home.view

import SharedWeatherViewModel
import com.example.skycast.home.viewmodel.HomeViewModel
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.TypedValue
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
import com.example.skycast.LocationGetter
import com.example.skycast.map.view.MapFragment
import com.example.skycast.R
import com.example.skycast.db.WeatherLocalDataSourceImpl
import com.example.skycast.home.viewmodel.HomeViewModelFactory
import com.example.skycast.model.DailyWeatherData
import com.example.skycast.model.HourlyWeatherData
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.model.remote.WeatherForecastResponse
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.network.WeatherRemoteDataSourceImpl
import com.example.skycast.setting.SettingsManager
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
    private lateinit var viewModel: HomeViewModel
    private lateinit var sharedWeatherViewModel: SharedWeatherViewModel
    private lateinit var settingsManager: SettingsManager

    // UI Components
    private lateinit var searchView: SearchView
    private lateinit var txtLocation: TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtDate: TextView
    private lateinit var imgWeather: ImageView
    private lateinit var txtTemp: TextView
    private lateinit var txtMin: TextView
    private lateinit var txtMax: TextView
    private lateinit var txtRain: TextView
    private lateinit var txtWind: TextView
    private lateinit var txtHumidity: TextView
    private lateinit var hourRecyclerView: RecyclerView
    private lateinit var forecastRecyclerView: RecyclerView
    private lateinit var btnAddLocation: ExtendedFloatingActionButton
    private lateinit var image_location: ImageView
    private lateinit var txt_rain:TextView
    private lateinit var txt_wind:TextView
    private lateinit var txt_humidity:TextView
    private lateinit var txt5DayForecast: TextView
    private lateinit var txtToday: TextView

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
        txtMin = view.findViewById(R.id.txt_min)
        txtMax = view.findViewById(R.id.txt_max)
        txtRain = view.findViewById(R.id.txt_rain)
        txtWind = view.findViewById(R.id.txt_wind)
        txtHumidity = view.findViewById(R.id.txt_humidity)
        hourRecyclerView = view.findViewById(R.id.hour_recycler_view)
        forecastRecyclerView = view.findViewById(R.id.recycler_view)
        btnAddLocation = view.findViewById(R.id.btn_add_location)
        image_location = view.findViewById(R.id.img_location)
        txt_rain = view.findViewById(R.id.rain)
        txt_wind = view.findViewById(R.id.wind)
        txt_humidity = view.findViewById(R.id.humidity)
        txt5DayForecast = view.findViewById(R.id.txt_5_day_forecast)
        txtToday = view.findViewById(R.id.txt_today)


        settingsManager = SettingsManager(requireContext())

        // Setup RecyclerViews
        hourRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        forecastRecyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize location manager and weather repository
        locationManager = LocationGetter(requireContext())
        weatherRepository = WeatherRepositoryImpl(WeatherRemoteDataSourceImpl(), WeatherLocalDataSourceImpl(requireContext()))

        // Initialize ViewModel
        val factory = HomeViewModelFactory(weatherRepository,requireContext())
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

        viewModel.currentWeatherByCity.observe(viewLifecycleOwner, Observer { currentWeather ->
            currentWeather?.let {
                updateCurrentWeather(it) // Update the UI with the current weather for the city
            }
        })

        btnAddLocation.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, MapFragment())
                addToBackStack(null) // Optional: Add to back stack
            }
        }
        image_location.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, MapFragment())
                addToBackStack(null)
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // Fetch weather for the entered city
                    viewModel.fetchWeatherByCity(it)

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally handle text change
                return true
            }
        })

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
                viewModel.fetchWeather(it.latitude, it.longitude) // Add language and units
            } ?: run {

            }
        }
    }

    private fun updateCurrentWeather(current: CurrentWetherResponse) {
        if(settingsManager.getUnit() == "metric") {
            txtTemp.text = "${current.main.temp.toInt()}°C"
            if (settingsManager.getLanguage() == "en") {

                txtMin.text = "${current.main.temp_min.toInt()}°C"
                txtMax.text = "${current.main.temp_max.toInt()}°C"
                val speedInKmh = current.wind.speed * 1.60934
                txtWind.text = "${speedInKmh.toInt()} k/h"
            } else if (settingsManager.getLanguage() == "ar" && settingsManager.getUnit() == "metric") {

                txtMin.text = "${current.main.temp_min.toInt()}°C"
                txtMax.text = "${current.main.temp_max.toInt()}°C"

                val speedInKmh = current.wind.speed * 1.60934
                txtWind.text = "${speedInKmh.toInt()} ك/س "
            }
        }
             if(settingsManager.getUnit() == "imperial") {
                 txtTemp.text = "${current.main.temp.toInt()}°F"
                 txtMin.text = "${current.main.temp_min.toInt()}°F"
                 txtMax.text = "${current.main.temp_max.toInt()}°F"
                 if (settingsManager.getLanguage() == "en") {
                     txtWind.text = "${current.wind.speed} m/h"

                 } else if (settingsManager.getLanguage() == "ar" && settingsManager.getUnit() == "imperial") {

                     txtWind.text = "${current.wind.speed} م/س "
                 }
             }
        if(settingsManager.getUnit() == "standard") {
            txtTemp.text = "${current.main.temp.toInt()}°K"
            txtMin.text = "${current.main.temp_min.toInt()}°K"
            txtMax.text = "${current.main.temp_max.toInt()}°K"
            if (settingsManager.getLanguage() == "en") {
                txtWind.text = "${current.wind.speed} m/s"

            }
                else if (settingsManager.getLanguage() == "ar" && settingsManager.getUnit() == "standard"){

                txtWind.text = "${current.wind.speed} م/ث"
            }
        }





        txtLocation.text = current.name
        txtStatus.text = current.weather[0].description
        val locale = if (settingsManager.getLanguage() == "ar") Locale("ar") else Locale.getDefault()
        val dateFormat = SimpleDateFormat("EEEE, MMM d", locale)
        txtDate.text = dateFormat.format(Date())
        txtHumidity.text = "${current.main.humidity}%"
        val layoutParamsToday = txtToday.layoutParams as ViewGroup.MarginLayoutParams
        val layoutParamsForecast = txt5DayForecast.layoutParams as ViewGroup.MarginLayoutParams
        if(settingsManager.getLanguage() == "ar"){
            txt_rain.text = "الأمطار"
            txt_wind.text = "سرعة الرياح"
            txt_humidity.text = "الرطوبة"
            txt5DayForecast.text = "توقعات الأسبوع"
            txtToday.text = "اليوم"

            layoutParamsToday.marginEnd = 40 // Set right margin
            txtToday.layoutParams = layoutParamsToday
            txtToday.textAlignment = View.TEXT_ALIGNMENT_VIEW_END

            // Set right margin and alignment for txt5DayForecast
            layoutParamsForecast.marginEnd = 40 // Set right margin
            txt5DayForecast.layoutParams = layoutParamsForecast
            txt5DayForecast.textAlignment = View.TEXT_ALIGNMENT_VIEW_END

            (txtToday.parent as ViewGroup).layoutDirection = View.LAYOUT_DIRECTION_LTR
            (txt5DayForecast.parent as ViewGroup).layoutDirection = View.LAYOUT_DIRECTION_RTL

            txtToday.requestLayout()
            txt5DayForecast.requestLayout()

        }



        imgWeather.setImageResource(getImage(current.weather[0].icon))
    }

    private fun updateForecastWeather(forecast: WeatherForecastResponse) {
        // Update UI components with forecast data
        txtRain.text = "${forecast.list[0].pop?.times(100)}%"

        val currentTimeInSeconds = System.currentTimeMillis() / 1000L // Get current time in seconds

        val hourlyData = forecast.list.filter {
            it.dt > currentTimeInSeconds // Keep only future timestamps
        }.take(8) // Take only the next 8 hours
            .map {
                HourlyWeatherData(
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it.dt * 1000L)),
                    temp = it.main.temp.toInt(),
                    description = it.weather.joinToString(", ") { it.description },
                    icon = it.weather[0].icon
                )
            }

        val dailyData = forecast.list.drop(8).groupBy { it.dt_txt?.substring(0, 10) }.map { entry ->
            DailyWeatherData(
                date = entry.key ?: "",
                maxTemp = entry.value.maxOf { it.main.temp_max.toInt() },
                minTemp = entry.value.minOf { it.main.temp_min.toInt() },
                icon = entry.value[0].weather[0].icon
            )
        }

        // Set adapters
        hourRecyclerView.adapter = HourlyForecastAdapter(hourlyData)
        forecastRecyclerView.adapter = DailyForecastAdapter(dailyData)
    }
}
