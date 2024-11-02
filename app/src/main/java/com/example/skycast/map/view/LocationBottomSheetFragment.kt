package com.example.skycast.map.view

import SharedWeatherViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skycast.R
import com.example.skycast.db.WeatherLocalDataSourceImpl
import com.example.skycast.home.view.HomeFragment
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
        val cloudTextView: TextView = view.findViewById(R.id.tv_cloud)
        val viewAllButton: Button = view.findViewById(R.id.btn_view_all)
        val saveWeatherButton: Button = view.findViewById(R.id.btn_save_weather)

        sharedWeatherViewModel = ViewModelProvider(requireActivity()).get(SharedWeatherViewModel::class.java)


        mapViewModel.currentWeather.observe(viewLifecycleOwner) { weather ->
            mapViewModel.weatherForecast.observe(viewLifecycleOwner) { forecast ->

                sharedWeatherViewModel.setWeatherData(weather, forecast)
                temperatureTextView.text = "${weather.main.temp}°C"
                cityTextView.text = weather.name
                cloudTextView.text = "Cloudiness: ${weather.clouds.all}%"
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

            if (currentWeather != null && weatherForecast != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        weatherRepository.insertWeather(weatherForecast)
                        weatherRepository.insertWeather(currentWeather)
                        Toast.makeText(requireContext(), "Weather saved successfully!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Failed to save weather: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Weather data is not available!", Toast.LENGTH_SHORT).show()
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

