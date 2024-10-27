package com.example.skycast.alert.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skycast.alert.view.Alarm

class AlarmViewModel : ViewModel() {
    private val _alarms = MutableLiveData<MutableList<Alarm>>(mutableListOf())
    val alarms: LiveData<MutableList<Alarm>> = _alarms

    fun addAlarm(alarm: Alarm) {
        _alarms.value?.add(alarm)
        _alarms.value = _alarms.value
    }
}

