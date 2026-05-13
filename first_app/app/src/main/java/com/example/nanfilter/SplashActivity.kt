package com.example.nanfilter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //defining a value which will hold the videoView
        val videoHolder = findViewById<VideoView>(R.id.splashVideo)
        val splashVideoPath = "android.resource://" + packageName + "/" + R.raw.design_two

        videoHolder.setVideoURI(Uri.parse(splashVideoPath))
        videoHolder.start() //starts the video

        // creates a delayed task
        Handler(Looper.getMainLooper()).postDelayed({
            //redirects to the main screen once the delay is over
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finish Splash Activity
        }, 8000) //timer and how long video will be displayed

        // hides the bar
        supportActionBar?.hide()
    }
}