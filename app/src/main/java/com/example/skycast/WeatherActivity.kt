package com.example.skycast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.skycast.fav.view.FavoriteFragment
import com.example.skycast.home.view.HomeFragment
import com.google.android.material.navigation.NavigationView

class WeatherActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        // Setup Toolbar
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
                    // Replace with HomeFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                }

                R.id.nav_fav -> {
                    // Replace with FavFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FavoriteFragment())
                        .commit()
                }

                R.id.nav_alram -> {
                    // Replace with AlarmFragment or another fragment
                    supportFragmentManager.beginTransaction()
//                        .replace(
//                            R.id.fragment_container,
//                          AlarmFragment()
//                        ) // Assume you have an AlarmFragment
                        .commit()
                }

                R.id.nav_settings -> {
                    // Replace with SettingsFragment
                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, SettingsFragment())
                        .commit()
                }

                R.id.nav_exit -> {
                    // Handle exit (e.g., finish the activity)
                    finish()
                }
            }

            // Close the drawer after an item is selected
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