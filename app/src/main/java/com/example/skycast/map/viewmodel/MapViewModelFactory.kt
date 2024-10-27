package com.example.skycast.map.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.model.WeatherRepositoryImpl

class MapViewModelFactory(private val weatherRepository: WeatherRepositoryImpl, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(weatherRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
