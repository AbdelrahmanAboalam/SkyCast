package com.example.skycast.alert.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("alarmId", 1)
        val alarmName = intent.getStringExtra("alarmName") ?: "Alarm"
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val hour = intent.getIntExtra("hour", 0)  // Retrieve the hour
        val minute = intent.getIntExtra("minute", 0)  // Retrieve the minute

        startAlarmService(context,alarmId, alarmName, latitude, longitude, hour, minute) // Pass hour and minute
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAlarmService(context: Context,alarmId: Int, alarmName: String, latitude: Double, longitude: Double, hour: Int, minute: Int) {
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarmId", alarmId)
            putExtra("alarmName", alarmName)
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            putExtra("hour", hour)  // Add hour to the intent
            putExtra("minute", minute)  // Add minute to the intent
        }
        context.startForegroundService(serviceIntent)
    }

}
