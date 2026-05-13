package com.example.memo_cards

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Splash_Screen_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        //defining a value which will hold the videoView
        val videoHolder = findViewById<VideoView>(R.id.splashVideo)
        val splashVideoPath = "android.resource://" + packageName + "/" + R.raw.loading_screen

        videoHolder.setVideoURI(Uri.parse(splashVideoPath))
        videoHolder.start() //starts the video

        // creates a delayed task
        Handler(Looper.getMainLooper()).postDelayed({
            //redirects to the main screen once the delay is over
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finish Splash Activity
        }, 4000) //timer and how long video will be displayed

        // hides the bar
        supportActionBar?.hide()
    }
}