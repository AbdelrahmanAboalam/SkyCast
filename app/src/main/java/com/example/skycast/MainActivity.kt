package com.example.skycast

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    lateinit var animation: Animation
    lateinit var txtView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animation = AnimationUtils.loadAnimation(this, R.anim.anima)
        txtView = findViewById(R.id.txtVieew)
        txtView.startAnimation(animation)
        txtView.postOnAnimationDelayed({
            val outIntent = Intent(this@MainActivity, WeatherActivity::class.java)
            startActivity(outIntent)
            finish()
        }, 100)


   }

}
