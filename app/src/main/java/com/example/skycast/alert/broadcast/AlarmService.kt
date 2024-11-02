package com.example.skycast.alert.broadcast

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.skycast.MainActivity
import com.example.skycast.R
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.model.remote.current.CurrentWetherResponse
import com.example.skycast.network.WeatherRemoteDataSourceImpl
import com.example.skycast.db.WeatherLocalDataSourceImpl
import com.example.skycast.setting.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var weatherRepository: WeatherRepository

    override fun onCreate() {
        super.onCreate()
        weatherRepository = WeatherRepositoryImpl(
            WeatherRemoteDataSourceImpl(),
            WeatherLocalDataSourceImpl(applicationContext)
        )
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmName = intent?.getStringExtra("alarmName") ?: "Alarm"
        val latitude = intent?.getDoubleExtra("latitude", 0.0) ?: 0.0
        val longitude = intent?.getDoubleExtra("longitude", 0.0) ?: 0.0

        // Play the alarm sound
        playAlarmSound()

        if (intent?.action == "DISMISS_ALARM") {
            dismissAlarm()
            return START_NOT_STICKY
        }

        // Fetch weather using the location
        fetchWeather(latitude, longitude, alarmName)

        return START_NOT_STICKY
    }

    private fun dismissAlarm() {
        // Cancel the notification
        stopAlarmSound()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll() // Cancels all notifications for this service
        stopSelf()
    }

    private fun fetchWeather(latitude: Double, longitude: Double, alarmName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPreferences = SettingsManager(applicationContext)
                val language = sharedPreferences.getLanguage() ?: "en"
                val unit = sharedPreferences.getUnit()

                val currentWeatherResponse: CurrentWetherResponse = weatherRepository.getCurrentWeather(latitude, longitude, language, unit)

                // Update UI on main thread
                launch(Dispatchers.Main) {
                    val currentTemp = currentWeatherResponse.main.temp // Adjust according to your API response
                    Toast.makeText(applicationContext, "Current Temperature: $currentTemp °C", Toast.LENGTH_SHORT).show()

                    // Setup the notification with the temperature
                    setupNotification(alarmName, currentTemp)
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupNotification(alarmName: String, currentTemp: Double) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel_id"

        // Create notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for alarm notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intents for notification actions
        val dismissIntent = Intent(this, AlarmService::class.java).apply {
            action = "DISMISS_ALARM"
        }
        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val openAppIntent = Intent(this, MainActivity::class.java).apply { // Replace YourMainActivity with your main activity
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Build and display the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app icon
            .setContentTitle("Alarm Triggered")
            .setContentText("Alarm: $alarmName\nCurrent Temperature: $currentTemp °C")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_launcher_foreground, "Dismiss", dismissPendingIntent) // Replace with your dismiss icon
            .addAction(R.drawable.alarm, "Open App", openAppPendingIntent) // Replace with your open app icon
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }


    private fun createForegroundNotification(alarmName: String): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel_id"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for alarm notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app icon
            .setContentTitle("Alarm Triggered")
            .setContentText("Alarm: $alarmName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true) // Make it ongoing
            .build()
    }

    private fun playAlarmSound() {
        // Play the default alarm sound
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, alarmSound)

        mediaPlayer?.apply {
            isLooping = true
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmSound()
    }

    private fun stopAlarmSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        Toast.makeText(this, "Alarm Dismissed", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
