package com.example.skycast.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.model.DailyWeatherData

class DailyForecastAdapter(private val dailyForecastList: List<DailyWeatherData>) :
    RecyclerView.Adapter<DailyForecastAdapter.DailyViewHolder>() {

    class DailyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val tempRangeTextView: TextView = itemView.findViewById(R.id.tempRangeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_forecast, parent, false)
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyWeather = dailyForecastList[position]
        holder.dateTextView.text = dailyWeather.date // Set the date
        holder.tempRangeTextView.text = "Max: ${dailyWeather.maxTemp}°C | Min: ${dailyWeather.minTemp}°C" // Set temperature range
    }

    override fun getItemCount(): Int {
        return dailyForecastList.size
    }
}
