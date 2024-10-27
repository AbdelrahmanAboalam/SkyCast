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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.alert.broadcast.AlarmReceiver
import com.example.skycast.alert.viewmodel.AlarmViewModel
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

        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)

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

        timePicker.setIs24HourView(true)

        AlertDialog.Builder(requireContext())
            .setTitle("Set Alarm")
            .setView(dialogView)
            .setPositiveButton("Set") { _, _ ->
                val hour = timePicker.hour
                val minute = timePicker.minute
                val name = nameEditText.text.toString()
                val time = String.format("%02d:%02d", hour, minute)
                val alarm = Alarm(name, time)
                viewModel.addAlarm(alarm)

                scheduleAlarm(hour, minute, name)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun scheduleAlarm(hour: Int, minute: Int, name: String) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("alarmName", name)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
