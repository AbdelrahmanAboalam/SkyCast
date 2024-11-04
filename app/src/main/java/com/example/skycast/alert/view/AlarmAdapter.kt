package com.example.skycast.alert.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R

class AlarmAdapter(private var alarms: List<Alarm>, private val onDeleteClickListener: (Alarm) -> Unit) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.alarmName)
        val time: TextView = itemView.findViewById(R.id.alarmTime)
        val deleteButton: ImageButton = itemView.findViewById(R.id.image_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.name.text = alarm.name
        holder.time.text = alarm.time
        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(alarm)
        }
    }

    override fun getItemCount(): Int = alarms.size

    fun updateAlarms(newAlarms: MutableList<Alarm>) {
        alarms = newAlarms
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}
