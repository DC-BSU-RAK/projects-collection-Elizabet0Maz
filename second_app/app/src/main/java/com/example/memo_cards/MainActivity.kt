package com.example.memo_cards

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var uriStorage: String = ""
    private lateinit var userNameInput: EditText
    private lateinit var nextButton: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    private val pickingMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // photo picker
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")


            //allows the app to have access to the storage
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flag)

            // pastes into imageHolder
            val imgHolder = findViewById<ImageView>(R.id.image_basic)
            imgHolder.setImageURI(uri)
            imgHolder.scaleType = ImageView.ScaleType.CENTER_CROP

            //converts uri to string and stores it in uriStorage
            uriStorage = uri.toString()

        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Finds the frame picture
        val Frame = findViewById<ImageView>(R.id.imgFrame)

        // When the frame is clicked the function gets activated
        Frame.setOnClickListener {
            //launches the photo picker and allow the user choose only images
            pickingMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        userNameInput = findViewById(R.id.userName)
        nextButton = findViewById(R.id.nextBtn)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        //gets the string from shared preferences
        val savedName = sharedPreferences.getString("userName", null)

        //if else statement made in order to make sure that user doesn't have to upload the image and user name everytime
        //if the string is empty
        if (savedName == null) {

            enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            // the main functionality for this activity
            val Frame = findViewById<ImageView>(R.id.imgFrame)
            userNameInput = findViewById(R.id.userName)
            nextButton = findViewById(R.id.nextBtn)

            // When the frame is clicked the function gets activated
            Frame.setOnClickListener {
                //launches the photo picker and allow the user choose only images
                pickingMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            nextButton.setOnClickListener {
                val inputText = userNameInput.text.toString()
                val editor = sharedPreferences.edit()

                editor.putString("userName", inputText)
                editor.putString("imageUri", uriStorage)
                editor.apply()

                val intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
                finish() // keeps users from going back
            }

        } else {
            //else if the string isnt empty teleport the user to the home screen
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}