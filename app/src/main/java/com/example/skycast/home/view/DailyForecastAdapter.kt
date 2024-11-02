package com.example.skycast.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.model.DailyWeatherData
import com.example.skycast.setting.SettingsManager
import java.util.Locale

class DailyForecastAdapter(private val dailyForecastList: List<DailyWeatherData>) :
    RecyclerView.Adapter<DailyForecastAdapter.DailyViewHolder>() {

    private lateinit var settingsManager: SettingsManager

    class DailyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val tempMinTextView: TextView = itemView.findViewById(R.id.txt_min)
        val tempMaxTextView: TextView = itemView.findViewById(R.id.txt_max)
        val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_daily_forecast, parent, false)
        settingsManager = SettingsManager(parent.context)
        return DailyViewHolder(view)
    }



    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyWeather = dailyForecastList[position]

        val locale = Locale.getDefault().language
        val minTemp = dailyWeather.minTemp.toString()
        val maxTemp = dailyWeather.maxTemp.toString()

        val formattedMinTemp: String
        val formattedMaxTemp: String

        val dayOfWeek = NumberUtils.getDayOfWeek(dailyWeather.date)
        holder.dateTextView.text = dayOfWeek

        if (locale == "ar") {
            formattedMinTemp = NumberUtils.convertToArabicNumerals(minTemp)
            formattedMaxTemp = NumberUtils.convertToArabicNumerals(maxTemp)
        } else {
            formattedMinTemp = minTemp
            formattedMaxTemp = maxTemp
        }

        when (settingsManager.getUnit()) {
            "imperial" -> {
                holder.tempMinTextView.text = "$formattedMinTemp°F"
                holder.tempMaxTextView.text = "$formattedMaxTemp°F"
            }

            "metric" -> {
                holder.tempMinTextView.text = "$formattedMinTemp°C"
                holder.tempMaxTextView.text = "$formattedMaxTemp°C"
            }

            "standard" -> {
                holder.tempMinTextView.text = "$formattedMinTemp°K"
                holder.tempMaxTextView.text = "$formattedMaxTemp°K"
            }
        }
        holder.weatherIcon.setImageResource(getImage(dailyWeather.icon))
    }

    override fun getItemCount(): Int {
        return dailyForecastList.size
    }
}