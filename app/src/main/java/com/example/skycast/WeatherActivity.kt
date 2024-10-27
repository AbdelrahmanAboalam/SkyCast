package com.example.skycast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.skycast.alert.view.AlarmFragment
import com.example.skycast.fav.view.FavoriteFragment
import com.example.skycast.home.view.HomeFragment
import com.example.skycast.setting.SettingsManager
import com.google.android.material.navigation.NavigationView

class WeatherActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        settingsManager = SettingsManager(this)
        settingsManager.initializeDefaults()

        settingsManager.setLanguage("en")

       val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment())
            transaction.commit()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            HomeFragment.isCurrentLocation=true
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                }

                R.id.nav_fav -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FavoriteFragment())
                        .commit()
                }

                R.id.nav_alram -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                          AlarmFragment()
                        )
                        .commit()
                }

                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction()
                        .commit()
                }

                R.id.nav_exit -> {
                    finish()
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}