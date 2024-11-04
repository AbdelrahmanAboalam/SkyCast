package com.example.skycast.alert.view

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val time: String,
    val hour: Int,
    val minute: Int,
    val latitude: Double,
    val longitude: Double
)
