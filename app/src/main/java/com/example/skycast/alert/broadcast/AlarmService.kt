package com.example.skycast.alert.broadcast

import android.annotation.SuppressLint
import android.app.AlarmManager
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
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.MainActivity
import com.example.skycast.R
import com.example.skycast.alert.view.Alarm
import com.example.skycast.alert.viewmodel.AlarmViewModel
import com.example.skycast.alert.viewmodel.AlarmViewModelFactory
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
    private val pendingIntentMap = mutableMapOf<Int, PendingIntent>()
    private lateinit var viewModel: AlarmViewModel


    override fun onCreate() {
        super.onCreate()
        weatherRepository = WeatherRepositoryImpl(
            WeatherRemoteDataSourceImpl(),
            WeatherLocalDataSourceImpl(applicationContext)

        )
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmSound != null) {
            mediaPlayer = MediaPlayer.create(this, alarmSound)
        }

    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val alarmId = intent?.getIntExtra("alarmId", 1) ?: 1
        val alarmName = intent?.getStringExtra("alarmName") ?: "Alarm"
        val latitude = intent?.getDoubleExtra("latitude", 0.0) ?: 0.0
        val longitude = intent?.getDoubleExtra("longitude", 0.0) ?: 0.0
        val hour = intent?.getIntExtra("hour", 0) ?: 0 // Retrieve the hour
        val minute = intent?.getIntExtra("minute", 0) ?: 0
        val requestCode = alarmName.hashCode()

        val alarm = Alarm(
            id = alarmId,
            name = alarmName,
            time = "$hour:$minute",
            hour = hour,
            minute = minute,
            latitude = latitude,
            longitude = longitude
        )

        CoroutineScope(Dispatchers.IO).launch {
            weatherRepository.deleteAlarm(alarm)
            // Optionally handle the success of the deletion here
        }




        val sharedPreferences = SettingsManager(applicationContext)
        val useAlarmSound = sharedPreferences.getNotificationType()
        if(useAlarmSound) {
            playAlarmSound()
        }

        if (intent?.action == "DISMISS_ALARM") {
            dismissAlarm(requestCode)
            return START_NOT_STICKY
        }

        fetchWeather(latitude, longitude, alarmName)

        return START_NOT_STICKY
    }

    private fun dismissAlarm(requestCode: Int) {


        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = pendingIntentMap[requestCode] ?: PendingIntent.getBroadcast(
            this,
            requestCode,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        pendingIntentMap.remove(requestCode)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        stopAlarmSound()
        notificationManager.cancelAll()

        stopForeground(true)
        stopSelf()
    }




    private fun fetchWeather(latitude: Double, longitude: Double, alarmName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPreferences = SettingsManager(applicationContext)
                val language = sharedPreferences.getLanguage() ?: "en"
                val unit = sharedPreferences.getUnit()

                val currentWeatherResponse: CurrentWetherResponse = weatherRepository.getCurrentWeather(latitude, longitude, language, unit)

                launch(Dispatchers.Main) {
                    val currentTemp = currentWeatherResponse.main.temp // Adjust according to your API response
                    Toast.makeText(applicationContext, "Current Temperature: $currentTemp °C", Toast.LENGTH_SHORT).show()

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for alarm notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val dismissIntent = Intent(this, AlarmService::class.java).apply {
            action = "DISMISS_ALARM"
        }
        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val openAppIntent = Intent(this, MainActivity::class.java).apply { // Replace YourMainActivity with your main activity
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Alarm Triggered")
            .setContentText("Alarm: $alarmName\nCurrent Temperature: $currentTemp °C")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_launcher_foreground, "Dismiss", dismissPendingIntent)
            .addAction(R.drawable.alarm, "Open App", openAppPendingIntent)
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
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Notification")
            .setContentText("Alarm: $alarmName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }

    private fun playAlarmSound() {
        // Play the default alarm sound

            mediaPlayer?.apply {
                start()
            }

    }
    private fun stopAlarmSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                Log.d("AlarmService", "Stopping alarm sound")
                it.stop()
            }
            it.release()
            mediaPlayer = null
        } ?: Log.d("AlarmService", "MediaPlayer is null")
        mediaPlayer?.stop()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}