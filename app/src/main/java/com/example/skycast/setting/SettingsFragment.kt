package com.example.skycast.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.R
import com.example.skycast.db.WeatherLocalDataSourceImpl
import com.example.skycast.model.WeatherRepository
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.network.WeatherRemoteDataSourceImpl
import com.example.skycast.setting.viewmodel.SettingViewModel
import com.example.skycast.setting.viewmodel.SettingViewModelFactory

class SettingsFragment : Fragment() {

    private lateinit var settingsManager: SettingsManager
    private  var onSettingsChangeListener: OnSettingsChangeListener?=null
    private lateinit var viewModel: SettingViewModel
    private lateinit var repository: WeatherRepository


    fun setOnSettingsChangeListener(listener: OnSettingsChangeListener) {
        onSettingsChangeListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        settingsManager = SettingsManager(requireContext())
        repository = WeatherRepositoryImpl(WeatherRemoteDataSourceImpl(), WeatherLocalDataSourceImpl(requireContext()))

        val factory = SettingViewModelFactory(repository, requireContext())
        viewModel = ViewModelProvider(this, factory).get(SettingViewModel::class.java)

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
                onSettingsChangeListener?.onLanguageChanged("ar")
                requireActivity().recreate()
                viewModel.fetchAllSettings()
            }
            else{
                englishSwitch.isChecked=true
                settingsManager.setLanguage("en")
                onSettingsChangeListener?.onLanguageChanged("en")
                requireActivity().recreate()
                viewModel.fetchAllSettings()
            }
        }

        englishSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                arabicSwitch.isChecked = false
                settingsManager.setLanguage("en")
                onSettingsChangeListener?.onLanguageChanged("en")
                requireActivity().recreate()
                viewModel.fetchAllSettings()
            }
            else{
                arabicSwitch.isChecked=true
                settingsManager.setLanguage("ar")
                onSettingsChangeListener?.onLanguageChanged("ar")
                requireActivity().recreate()
                viewModel.fetchAllSettings()
            }
        }
    }

    private fun setupUnitSwitches(view: View) {
        val metricSwitch: Switch = view.findViewById(R.id.k_m)
        val standardSwitch: Switch = view.findViewById(R.id.m_s)
        val imperialSwitch: Switch = view.findViewById(R.id.m_h)
        val celsiusSwitch: Switch = view.findViewById(R.id.C)
        val kelvinSwitch: Switch = view.findViewById(R.id.K)
        val fahrenheitSwitch: Switch = view.findViewById(R.id.F)

        // Set initial states based on saved settings
        updateUnitSwitches(settingsManager.getUnit(), view)

        metricSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateUnitPreferences("metric", view=view)
                updateUnitSwitches("metric", view=view)
                viewModel.fetchAllSettings()
            }
        }

        standardSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateUnitPreferences("standard", view=view)
                updateUnitSwitches("standard", view=view)
                viewModel.fetchAllSettings()
            }
        }

        imperialSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateUnitPreferences("imperial", view=view)
                updateUnitSwitches("imperial", view=view)
                viewModel.fetchAllSettings()
            }
        }

        celsiusSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateUnitPreferences("metric", true, view=view)
                updateUnitSwitches("metric", view=view)
                viewModel.fetchAllSettings()
            }
        }

        kelvinSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateUnitPreferences("standard", true, view=view)
                updateUnitSwitches("standard", view=view)
                viewModel.fetchAllSettings()
            }
        }

        fahrenheitSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateUnitPreferences("imperial", true, view=view)
                updateUnitSwitches("imperial", view=view)
                viewModel.fetchAllSettings()
            }
        }
    }

    private fun updateUnitPreferences(unit: String, updateUI: Boolean = false, view: View) {
        when (unit) {
            "metric" -> {
                settingsManager.setUnit("metric")
                // Update the UI state
                updateUnitSwitches("metric", view=view)
            }
            "standard" -> {
                settingsManager.setUnit("standard")
                updateUnitSwitches("standard", view=view)
            }
            "imperial" -> {
                settingsManager.setUnit("imperial")
                updateUnitSwitches("imperial", view=view)
            }
        }

        // If updateUI is true, ensure the UI reflects the change
        if (updateUI) {
            updateUnitSwitches(unit, view)
        }
    }

    private fun updateUnitSwitches(currentUnit: String, view: View) {
        val metricSwitch: Switch = view.findViewById(R.id.k_m)
        val standardSwitch: Switch = view.findViewById(R.id.m_s)
        val imperialSwitch: Switch = view.findViewById(R.id.m_h)
        val celsiusSwitch: Switch = view.findViewById(R.id.C)
        val kelvinSwitch: Switch = view.findViewById(R.id.K)
        val fahrenheitSwitch: Switch = view.findViewById(R.id.F)

        // Reset all switches
        metricSwitch.isChecked = currentUnit == "metric"
        standardSwitch.isChecked = currentUnit == "standard"
        imperialSwitch.isChecked = currentUnit == "imperial"

        // Set specific states for temperature units
        celsiusSwitch.isChecked = currentUnit == "metric"
        kelvinSwitch.isChecked = currentUnit == "standard"
        fahrenheitSwitch.isChecked = currentUnit == "imperial"
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
