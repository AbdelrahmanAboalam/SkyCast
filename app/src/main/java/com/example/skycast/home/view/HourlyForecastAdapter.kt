package com.example.skycast.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.model.HourlyWeatherData

class HourlyForecastAdapter(private val hourlyForecastList: List<HourlyWeatherData>) :
    RecyclerView.Adapter<HourlyForecastAdapter.HourlyViewHolder>() {

    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_forecast, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourlyWeather = hourlyForecastList[position]
        holder.dateTextView.text = hourlyWeather.time
        holder.tempTextView.text = "${hourlyWeather.temp}°C"
        holder.descriptionTextView.text = hourlyWeather.description
        holder.weatherIcon.setImageResource(getImage(hourlyWeather.icon))
    }

    override fun getItemCount(): Int {
        return hourlyForecastList.size
    }
}

