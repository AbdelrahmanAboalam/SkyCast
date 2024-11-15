package com.example.skycast.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.model.HourlyWeatherData
import com.example.skycast.setting.SettingsManager
import java.util.Locale

class HourlyForecastAdapter(private val hourlyForecastList: List<HourlyWeatherData>) :
    RecyclerView.Adapter<HourlyForecastAdapter.HourlyViewHolder>() {
        private lateinit var settingsManager: SettingsManager

    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_forecast, parent, false)
        settingsManager = SettingsManager(parent.context)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourlyWeather = hourlyForecastList[position]
        val locale = Locale.getDefault().language
        val temp = hourlyWeather.temp.toString()

        val formattedTemp: String = if (locale == "ar") {
            NumberUtils.convertToArabicNumerals(temp)
        } else {
            temp
        }

        if(settingsManager.getUnit() == "metric") {
            holder.tempTextView.text = "$formattedTemp°C"
        }
        else if(settingsManager.getUnit() == "imperial") {
            holder.tempTextView.text = "$formattedTemp°F"
        }
        else if(settingsManager.getUnit() == "standard") {
            holder.tempTextView.text = "$formattedTemp°K"
        }

        holder.dateTextView.text = hourlyWeather.time
        holder.descriptionTextView.text = hourlyWeather.description
        holder.weatherIcon.setImageResource(getImage(hourlyWeather.icon))
    }

    override fun getItemCount(): Int {
        return hourlyForecastList.size
    }
}

