package com.example.skycast.location

import SharedWeatherViewModel
import com.example.skycast.home.viewmodel.HomeViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.R
import com.example.skycast.db.WeatherLocalDataSourceImpl
import com.example.skycast.home.view.HomeFragment
import com.example.skycast.map.MapViewModel
import com.example.skycast.map.MapViewModelFactory
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.network.WeatherRemoteDataSource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

        // Initialize Weather Repository and MapViewModel
        weatherRepository = WeatherRepositoryImpl(
            WeatherRemoteDataSource(),
            WeatherLocalDataSourceImpl(requireContext())
        )
        val factory = MapViewModelFactory(weatherRepository)
        mapViewModel = ViewModelProvider(this, factory).get(MapViewModel::class.java)

        // UI elements
        val temperatureTextView: TextView = view.findViewById(R.id.tv_temperature)
        val cityTextView: TextView = view.findViewById(R.id.tv_city)
        val cloudTextView: TextView = view.findViewById(R.id.tv_cloud)
        val viewAllButton: Button = view.findViewById(R.id.btn_view_all)

        sharedWeatherViewModel = ViewModelProvider(requireActivity()).get(SharedWeatherViewModel::class.java)


        // Observe current weather data
        mapViewModel.currentWeather.observe(viewLifecycleOwner) { weather ->
            mapViewModel.weatherForecast.observe(viewLifecycleOwner) { forecast ->

                sharedWeatherViewModel.setWeatherData(weather, forecast)
                temperatureTextView.text = "${weather.main.temp}°C"
                cityTextView.text = weather.name
                cloudTextView.text = "Cloudiness: ${weather.clouds.all}%"
            }
        }

        // Fetch the weather when fragment is loaded
        mapViewModel.setLocation(latitude ?: 0.0, longitude ?: 0.0, "en", "metric")

        // Handle view all button click
        viewAllButton.setOnClickListener {
            HomeFragment.isCurrentLocation = false
            // Navigate to HomeFragment directly
            parentFragmentManager.commit {
                replace(R.id.fragment_container, HomeFragment())
                addToBackStack(null)
            }
            dismiss()
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

