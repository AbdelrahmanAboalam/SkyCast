package com.example.skycast.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.example.skycast.R

class SettingsFragment : Fragment() {

    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        settingsManager = SettingsManager(requireContext())

        setupLanguageSwitches(view)
        setupUnitSwitches(view)
        setupNotificationSwitches(view)

        return view
    }

    private fun setupLanguageSwitches(view: View) {
        val arabicSwitch: Switch = view.findViewById(R.id.switch_language_arabic)
        val englishSwitch: Switch = view.findViewById(R.id.switch_language_english)

        // Set initial states based on saved settings
        arabicSwitch.isChecked = settingsManager.getLanguage() == "ar"
        englishSwitch.isChecked = settingsManager.getLanguage() == "en"

        arabicSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                englishSwitch.isChecked = false
                settingsManager.setLanguage("ar")
            }
        }

        englishSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                arabicSwitch.isChecked = false
                settingsManager.setLanguage("en")
            }
        }
    }

    private fun setupUnitSwitches(view: View) {
        val metricSwitch: Switch = view.findViewById(R.id.switch_unit_metric)
        val standardSwitch: Switch = view.findViewById(R.id.switch_unit_standard)

        // Set initial states based on saved settings
        metricSwitch.isChecked = settingsManager.getUnit() == "metric"
        standardSwitch.isChecked = settingsManager.getUnit() == "standard"

        metricSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                standardSwitch.isChecked = false
                settingsManager.setUnit("metric")
            }
        }

        standardSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                metricSwitch.isChecked = false
                settingsManager.setUnit("standard")
            }
        }
    }

    private fun setupNotificationSwitches(view: View) {
        val enabledSwitch: Switch = view.findViewById(R.id.switch_notification_enabled)
        val disabledSwitch: Switch = view.findViewById(R.id.switch_notification_disabled)

        // Set initial state based on saved settings
        enabledSwitch.isChecked = settingsManager.getNotificationType()
        disabledSwitch.isChecked = !settingsManager.getNotificationType()

        enabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                disabledSwitch.isChecked = false
                settingsManager.setNotificationType(true)
            }
        }

        disabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enabledSwitch.isChecked = false
                settingsManager.setNotificationType(false)
            }
        }
    }
}
