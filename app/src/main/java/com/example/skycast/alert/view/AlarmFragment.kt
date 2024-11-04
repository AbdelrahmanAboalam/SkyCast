package com.example.skycast.alert.view

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.LocationGetter
import com.example.skycast.R
import com.example.skycast.alert.broadcast.AlarmReceiver
import com.example.skycast.alert.viewmodel.AlarmViewModel
import com.example.skycast.alert.viewmodel.AlarmViewModelFactory
import com.example.skycast.db.WeatherLocalDataSourceImpl
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmFragment : Fragment() {
    private lateinit var viewModel: AlarmViewModel
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var locationGetter: LocationGetter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.alarmRecyclerView)
        val btnAddAlarm: Button = view.findViewById(R.id.btnAddAlarm)



        // Create the WeatherRepository instance or inject it as needed
        val weatherRepository = WeatherRepositoryImpl(WeatherRemoteDataSourceImpl(), WeatherLocalDataSourceImpl(requireContext()))

        // Create the ViewModelFactory with necessary parameters
        val viewModelFactory = AlarmViewModelFactory(weatherRepository, requireContext())

        // Use ViewModelProvider with the factory
        viewModel = ViewModelProvider(this, viewModelFactory).get(AlarmViewModel::class.java)

        viewModel.fetchAlarms()

        alarmAdapter = AlarmAdapter(emptyList()) { alarm ->
           deleteAlarm(alarm)
        }

        recyclerView.adapter = alarmAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())



        setObservables()



        btnAddAlarm.setOnClickListener {
            fetchLocationAndShowDialog()
        }

        checkAlarmPermissions()

        return view
    }

    private fun deleteAlarm(alarm: Alarm) {
        // Delete the alarm from the ViewModel
        viewModel.deleteAlarm(alarm)

        // Stop the scheduled alarm in AlarmManager
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarm.id)
        }

        val requestCode = alarm.name.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE // Use NO_CREATE to get the existing PendingIntent without creating a new one
        )

        // Cancel the alarm
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel() // Also cancel the PendingIntent
        }

        // Optionally, you can notify the user
        Toast.makeText(requireContext(), "Alarm deleted", Toast.LENGTH_SHORT).show()

        // Optionally, refresh the alarms list after deletion
        viewModel.fetchAlarms()
    }

    private fun checkAlarmPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    private fun fetchLocationAndShowDialog() {
        val locationGetter = LocationGetter(requireContext())
        if (locationGetter.hasLocationPermission()) {
            // Use a coroutine to fetch the location
            viewLifecycleOwner.lifecycleScope.launch {
                val location = locationGetter.getLocation()
                if (location != null) {
                    // Fetch weather after getting location
                    viewModel.fetchWeather(location.latitude, location.longitude)
                    showAlarmDialog()
                } else {
                    Toast.makeText(requireContext(), "Unable to get location. Check permissions.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Location permission not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlarmDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_alarm, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.alarmNamePicker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.alarmTimePicker)

        timePicker.setIs24HourView(false)

        AlertDialog.Builder(requireContext())
            .setTitle("Set Alarm")
            .setView(dialogView)
            .setPositiveButton("Set") { _, _ ->
                val hour = timePicker.hour
                val minute = timePicker.minute
                val name = nameEditText.text.toString()

                // Fetch location and schedule alarm
                val locationGetter = LocationGetter(requireContext())
                CoroutineScope(Dispatchers.Main).launch {
                    val location = locationGetter.getLocation()
                    if (location != null) {
                        // Create Alarm object
                        val alarm = Alarm(
                            name = name,
                            time = "$hour:$minute",
                            hour = hour,
                            minute = minute,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        viewModel.addAlarm(alarm)
                        viewModel.insertAlarm(alarm)
                        viewModel.alarms.value?.add(alarm)
                        // Fetch all alarms to update the list
                        viewModel.fetchAlarms() // Fetch alarms after adding a new one
                        viewModel.alarms.value?.let { alarmAdapter.updateAlarms(it) }

                        // Schedule the alarm
                        scheduleAlarm(alarm)
                    } else {
                        Toast.makeText(requireContext(), "Unable to get location. Check permissions.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun scheduleAlarm(alarm: Alarm) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarm.id)
            putExtra("alarmName", alarm.name)
            putExtra("latitude", alarm.latitude)
            putExtra("longitude", alarm.longitude)
            putExtra("hour", alarm.hour)
            putExtra("minute", alarm.minute)
        }

        // Use the hash code of the alarm name for a unique request code
        val requestCode = alarm.name.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the calendar time for the alarm
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)

            // If the time is in the past, set it for the next day
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Schedule the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        // Optionally, save the PendingIntent for future reference
        viewModel.savePendingIntent(requestCode, pendingIntent)
    }

    fun setObservables() {
        viewModel.currentWeather.observe(viewLifecycleOwner) { currentWeather ->
            // Handle the current weather data here
        }
        viewModel.alarms.observe(viewLifecycleOwner) { alarms ->
            alarmAdapter.updateAlarms(alarms)
        }

    }
}
