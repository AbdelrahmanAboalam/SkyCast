package com.example.skycast.alert.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.model.WeatherRepository

class AlarmViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            return AlarmViewModel(weatherRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

