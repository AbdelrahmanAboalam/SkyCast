package com.example.skycast.fav.view

import androidx.recyclerview.widget.DiffUtil
import com.example.skycast.model.remote.current.CurrentWetherResponse

class WeatherDiffCallback(
    private val oldList: List<CurrentWetherResponse>,
    private val newList: List<CurrentWetherResponse>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare unique identifiers, such as idKey
        return oldList[oldItemPosition].idKey == newList[newItemPosition].idKey
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare the content of items
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}