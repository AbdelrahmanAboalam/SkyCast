package com.example.skycast.home.view

import com.example.skycast.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

object NumberUtils {
    // Function to convert Western numerals to Arabic numerals
    fun convertToArabicNumerals(number: String): String {
        val arabicNumerals = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val stringBuilder = StringBuilder()

        for (char in number) {
            if (char in '0'..'9') {
                stringBuilder.append(arabicNumerals[char - '0'])
            } else {
                stringBuilder.append(char)
            }
        }
        return stringBuilder.toString()
    }

    fun getDayOfWeek(dateString: String): String {
        // Adjust the date format as necessary for your input
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // Full day name
        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            // Handle parsing error
            "Unknown"
        }
    }
}
