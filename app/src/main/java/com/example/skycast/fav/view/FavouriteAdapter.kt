package com.example.skycast.fav.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.home.view.getImage
import com.example.skycast.model.remote.current.CurrentWetherResponse


class FavouriteAdapter(
    private val onFavoriteClick: (CurrentWetherResponse) -> Unit,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    private var favList: List<CurrentWetherResponse> = emptyList()

    // Function to update the weather list with DiffUtil
    fun setWeatherList(newList: List<CurrentWetherResponse>) {
        val diffCallback = WeatherDiffCallback(favList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        favList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fav_layout, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(favList[position])
    }

    override fun getItemCount(): Int = favList.size

    inner class FavouriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityName: TextView = itemView.findViewById(R.id.cityName)
        private val tempMin: TextView = itemView.findViewById(R.id.tempMin)
        private val tempMax: TextView = itemView.findViewById(R.id.tempMax)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.image_button)

        fun bind(currentWeather: CurrentWetherResponse) {
            cityName.text = currentWeather.name
            tempMin.text = currentWeather.main.temp_min.toString()
            tempMax.text = currentWeather.main.temp_max.toString()

            // Load weather icon
            weatherIcon.setImageResource(getImage(currentWeather.weather[0].icon))

            // Handle favorite button click
            btnFavorite.setOnClickListener {
                onFavoriteClick(currentWeather)
            }

            weatherIcon.setOnClickListener {
                onImageClick(currentWeather.idKey)
            }
        }
    }
}