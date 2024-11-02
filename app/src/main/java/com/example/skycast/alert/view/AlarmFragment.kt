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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.alarmRecyclerView)
        val btnAddAlarm: Button = view.findViewById(R.id.btnAddAlarm)

        // Create the WeatherRepository instance or inject it as needed
        val weatherRepository = WeatherRepositoryImpl( WeatherRemoteDataSourceImpl(),WeatherLocalDataSourceImpl(requireContext()))

        // Create the ViewModelFactory with necessary parameters
        val viewModelFactory = AlarmViewModelFactory(weatherRepository, requireContext())

        // Use ViewModelProvider with the factory
        viewModel = ViewModelProvider(this, viewModelFactory).get(AlarmViewModel::class.java)

        alarmAdapter = AlarmAdapter(emptyList())
        recyclerView.adapter = alarmAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.alarms.observe(viewLifecycleOwner, { alarms ->
            alarmAdapter = AlarmAdapter(alarms)
            recyclerView.adapter = alarmAdapter
        })

        btnAddAlarm.setOnClickListener {
            showAlarmDialog()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        return view
    }

    private fun showAlarmDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_alarm, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.alarmNamePicker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.alarmTimePicker)

        // Set TimePicker to 12-hour format
        timePicker.setIs24HourView(false)

        AlertDialog.Builder(requireContext())
            .setTitle("Set Alarm")
            .setView(dialogView)
            .setPositiveButton("Set") { _, _ ->
                var hour = timePicker.hour
                val minute = timePicker.minute
                val name = nameEditText.text.toString()

                // Determine AM or PM
                val amPm = if (hour >= 12) "PM" else "AM"
                if (hour > 12) {
                    hour -= 12
                } else if (hour == 0) {
                    hour = 12
                }

                val time = String.format("%02d:%02d %s", hour, minute, amPm)
                val alarm = Alarm(name, time)
                viewModel.addAlarm(alarm)

                // Retrieve current location
                val locationGetter = LocationGetter(requireContext())
                CoroutineScope(Dispatchers.Main).launch {
                    val location = locationGetter.getLocation()
                    if (location != null) {
                        // Schedule the alarm with location data
                        scheduleAlarm(timePicker.hour, minute, name, location.latitude, location.longitude)
                    } else {
                        Toast.makeText(requireContext(), "Unable to get location. Check permissions.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun scheduleAlarm(hour: Int, minute: Int, name: String, latitude: Double, longitude: Double) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("alarmName", name)
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }

        val requestCode = name.hashCode() // or use any unique identifier
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(requireContext(), "Alarm scheduled for $hour:$minute", Toast.LENGTH_SHORT).show()

        // Save PendingIntent for future cancellation
        viewModel.savePendingIntent(requestCode, pendingIntent)
    }




}
