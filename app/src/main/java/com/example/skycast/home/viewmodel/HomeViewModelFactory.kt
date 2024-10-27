package com.example.skycast.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.model.WeatherRepository

class HomeViewModelFactory(private val weatherRepository: WeatherRepository,private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(weatherRepository,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
