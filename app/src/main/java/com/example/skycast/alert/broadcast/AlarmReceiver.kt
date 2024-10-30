package com.example.skycast.alert.broadcast


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi



class AlarmReceiver : BroadcastReceiver() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val alarmName = intent.getStringExtra("alarmName") ?: "Alarm"
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        startAlarmService(context, alarmName, latitude, longitude)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAlarmService(context: Context, alarmName: String, latitude: Double, longitude: Double) {
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarmName", alarmName)
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }
        context.startService(serviceIntent)
    }


}
