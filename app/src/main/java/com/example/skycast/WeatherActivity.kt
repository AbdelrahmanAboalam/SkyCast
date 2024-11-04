package com.example.skycast

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.skycast.alert.view.AlarmFragment
import com.example.skycast.db.Cashing
import com.example.skycast.fav.view.FavoriteFragment
import com.example.skycast.home.view.HomeFragment
import com.example.skycast.setting.OnSettingsChangeListener
import com.example.skycast.setting.SettingsFragment
import com.example.skycast.setting.SettingsManager
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class WeatherActivity : AppCompatActivity(), OnSettingsChangeListener {

    private lateinit var settingsManager: SettingsManager
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_weather)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.tool) // Use your color resource
        }

        settingsManager = SettingsManager(this)
        settingsManager.initializeDefaults()

        val cashing = Cashing(this)
        cashing.initializeCache()

//        val currentLocale = Locale.getDefault().language
//        if (settingsManager.getLanguage() != "en") {
//            settingsManager.setLanguage(currentLocale)
//        }
        updateLocale(Locale(settingsManager.getLanguage()))

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        updateNavigationMenuTitles()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment(),R.id.nav_home)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment(),R.id.nav_home)
                }
                R.id.nav_fav -> {
                    loadFragment(FavoriteFragment(),R.id.nav_fav)
                }
                R.id.nav_alram -> {
                    loadFragment(AlarmFragment(),R.id.nav_alram)
                }
                R.id.nav_settings -> {
                    val settingsFragment = SettingsFragment()
                    settingsFragment.setOnSettingsChangeListener(this)
                    loadFragment(settingsFragment,R.id.nav_settings)
                }
                R.id.nav_exit -> {
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun loadFragment(fragment: Fragment,menuItemId: Int) {
        navigationView.setCheckedItem(menuItemId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }

        if (fragment is HomeFragment) {
            finish()
        } else {
            loadFragment(HomeFragment(),R.id.nav_home)
        }
    }

    override fun onLanguageChanged(newLanguage: String) {
        settingsManager.setLanguage(newLanguage) // Ensure you save the new language in the settings manager
        updateLocale(Locale(newLanguage))
        updateNavigationMenuTitles()
        recreate() // Recreate activity to apply changes
    }

    private fun updateNavigationMenuTitles() {
        val isArabic = settingsManager.getLanguage() == "ar"
        if (isArabic) {
            navigationView.menu.findItem(R.id.nav_home).setTitle("الرئيسية")
            navigationView.menu.findItem(R.id.nav_fav).setTitle("المفضلة")
            navigationView.menu.findItem(R.id.nav_alram).setTitle("الاشعارات")
            navigationView.menu.findItem(R.id.nav_settings).setTitle("الإعدادات")
            navigationView.menu.findItem(R.id.nav_exit).setTitle("خروج")
        } else {
            navigationView.menu.findItem(R.id.nav_home).setTitle("Home")
            navigationView.menu.findItem(R.id.nav_fav).setTitle("Favorites")
            navigationView.menu.findItem(R.id.nav_alram).setTitle("Alarms")
            navigationView.menu.findItem(R.id.nav_settings).setTitle("Settings")
            navigationView.menu.findItem(R.id.nav_exit).setTitle("Exit")
        }
    }

    private fun updateLocale(locale: Locale) {
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


}
