package com.example.skycast.map.view

import SharedWeatherViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skycast.R
import com.example.skycast.db.WeatherLocalDataSourceImpl
import com.example.skycast.home.view.HomeFragment
import com.example.skycast.home.view.getImage
import com.example.skycast.map.viewmodel.MapViewModel
import com.example.skycast.map.viewmodel.MapViewModelFactory
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.network.WeatherRemoteDataSourceImpl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class LocationBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var weatherRepository: WeatherRepositoryImpl
    private lateinit var sharedWeatherViewModel: SharedWeatherViewModel


    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble(ARG_LATITUDE)
            longitude = it.getDouble(ARG_LONGITUDE)
        }
        HomeFragment.isCurrentLocation = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherRepository = WeatherRepositoryImpl(
            WeatherRemoteDataSourceImpl(),
            WeatherLocalDataSourceImpl(requireContext())
        )
        val factory = MapViewModelFactory(weatherRepository, requireContext())
        mapViewModel = ViewModelProvider(this, factory).get(MapViewModel::class.java)

        val temperatureTextView: TextView = view.findViewById(R.id.tv_temperature)
        val cityTextView: TextView = view.findViewById(R.id.tv_city)
        val cloudImage: ImageView = view.findViewById(R.id.img_weather_icon)
        val viewAllButton: ImageButton = view.findViewById(R.id.btn_view_all)
        val saveWeatherButton: ImageButton = view.findViewById(R.id.btn_save_weather)

        sharedWeatherViewModel = ViewModelProvider(requireActivity()).get(SharedWeatherViewModel::class.java)


        mapViewModel.currentWeather.observe(viewLifecycleOwner) { weather ->
            mapViewModel.weatherForecast.observe(viewLifecycleOwner) { forecast ->

                sharedWeatherViewModel.setWeatherData(weather, forecast)
                temperatureTextView.text = "${weather.main.temp}°C"
                cityTextView.text = weather.name
                cloudImage.setImageResource(getImage(weather.weather[0].main))

            }
        }

        mapViewModel.setLocation(latitude ?: 0.0, longitude ?: 0.0)

        viewAllButton.setOnClickListener {
            HomeFragment.isCurrentLocation = false
            parentFragmentManager.commit {
                replace(R.id.fragment_container, HomeFragment())
                addToBackStack(null)
            }
            dismiss()
        }
        saveWeatherButton.setOnClickListener {
            val currentWeather = mapViewModel.currentWeather.value
            val weatherForecast = mapViewModel.weatherForecast.value
            saveWeatherButton.setImageResource(R.drawable.heart_filled)

            if (currentWeather != null && weatherForecast != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        weatherRepository.insertWeather(weatherForecast)
                        weatherRepository.insertWeather(currentWeather)
                    } catch (e: Exception) {
                    }
                }
            } else {
            }
        }
    }

        companion object {
            private const val ARG_LATITUDE = "latitude"
            private const val ARG_LONGITUDE = "longitude"

            fun newInstance(latitude: Double, longitude: Double): LocationBottomSheetFragment {
                val fragment = LocationBottomSheetFragment()
                val args = Bundle().apply {
                    putDouble(ARG_LATITUDE, latitude)
                    putDouble(ARG_LONGITUDE, longitude)
                }
                fragment.arguments = args
                return fragment
            }
        }
    override fun onDestroy() {
        super.onDestroy()
        HomeFragment.isCurrentLocation = true
    }
    }

