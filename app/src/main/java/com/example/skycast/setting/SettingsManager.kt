package com.example.skycast.setting

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun initializeDefaults() {
        if (sharedPreferences.getBoolean("first_launch", true)) {
            setLanguage("en")
            setUnit("metric")
            setNotificationType(false)
            setFirstLaunch(false)
        }
    }

    private fun setFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferences.edit().putBoolean("first_launch", isFirstLaunch).apply()
    }

    fun setLanguage(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString("language", "en") ?: "en"
    }

    fun setUnit(unit: String) {
        sharedPreferences.edit().putString("unit", unit).apply()
    }

    fun getUnit(): String {
        return sharedPreferences.getString("unit", "metric") ?: "metric"
    }

    fun setNotificationType(type: Boolean) {
        sharedPreferences.edit().putBoolean("notification_type", type).apply()
    }

    fun getNotificationType(): Boolean {
        return sharedPreferences.getBoolean("notification_type", false) ?: false
    }
}
