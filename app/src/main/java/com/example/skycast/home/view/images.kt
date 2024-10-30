package com.example.skycast.home.view

import com.example.skycast.R

fun getImage(str : String) : Int{
    return when(str){
        "01d" -> R.drawable.sunny
        "01n" -> R.drawable.sunny
        "02d" -> R.drawable.cloudy_sunny
        "02n" -> R.drawable.cloudy_sunny
        "03d" -> R.drawable.cloudy_sunny
        "03n" -> R.drawable.cloudy_sunny
        "04d" -> R.drawable.cloudy
        "04n" -> R.drawable.cloudy
        "09d" -> R.drawable.rainy
        "09n" -> R.drawable.rainy
        "10d" -> R.drawable.rainy
        "10n" -> R.drawable.rainy
        "11d" -> R.drawable.storm
        "11n" -> R.drawable.storm
        "13d" -> R.drawable.snowy
        "13n" -> R.drawable.snowy
        "50d" -> R.drawable.windy
        "50n" -> R.drawable.windy
        else -> R.drawable.sunny

    }
}