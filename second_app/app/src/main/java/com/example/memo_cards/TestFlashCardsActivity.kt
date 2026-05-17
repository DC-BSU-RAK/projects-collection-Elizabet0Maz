package com.example.memo_cards

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TestFlashCardsActivity : AppCompatActivity() {


    private lateinit var showAnsBtn: Button
    private lateinit var nextBtn: ImageView
    private lateinit var cardText: TextView
    private lateinit var feedbackGroup: LinearLayout


    // holds all the cards saved for this specific group
    private lateinit var flashCardList: ArrayList<FlashCard>

    //counter which stores the index of a card user is on
    private var currentCardIndex = 0
    private var correctAns = 0 //correct answers counter

    // Load new question function
    private fun loadNewQuestion() {
        // checks if the current index is still lesser than the actual list
        if (currentCardIndex < flashCardList.size) {

            //displays the current index of the question
            cardText.text = flashCardList[currentCardIndex].question

            // hides unnecessary buttons
            showAnsBtn.visibility = android.view.View.VISIBLE
            feedbackGroup.visibility = android.view.View.GONE
            nextBtn.visibility = android.view.View.GONE

        } else {
            //if the cards are not less than the index display a score
            cardText.text = "Your Total score is\n($correctAns / ${flashCardList.size})!!!"

            // hide all the buttons
            showAnsBtn.visibility = android.view.View.GONE
            feedbackGroup.visibility = android.view.View.GONE
            nextBtn.visibility = android.view.View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test_flash_cards)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // button variables
        val backArrow = findViewById<ImageView>(R.id.backBtn)
        val correctBtn : Button = findViewById(R.id.correct)
        val wrongBtn : Button = findViewById(R.id.wrong)

        //linking xml elements
        cardText = findViewById(R.id.ansQue)
        showAnsBtn = findViewById(R.id.showAns)
        nextBtn = findViewById(R.id.imageView11)
        feedbackGroup = findViewById(R.id.feedBack)

        //arrow back to go back to the homepage
        backArrow.setOnClickListener {
            finish()
        }

        //shared preferences and gson
        val sharedPreferences : android.content.SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = com.google.gson.Gson()

        // requests the title from the shared preferences
        val savedTitle = sharedPreferences.getString("clickedTitle", "")

        // grabs the json string of cards using group key
        val json = sharedPreferences.getString("${savedTitle}_cards", null)



        // gets the type of array list
        val type: Type = object : TypeToken<ArrayList<FlashCard>>() {}.type

        // checking below if the array list is empty or not
        if (json != null) {
            // getting data from gson and save it to array list
            flashCardList = gson.fromJson(json, type)
        } else {
            flashCardList = ArrayList()
        }


        // instead of the adapter start a new question
        if (flashCardList.isNotEmpty()) {
            //calls the quiz function
            loadNewQuestion()
        } else {
            android.widget.Toast.makeText(this, "Add some cards first. . .", android.widget.Toast.LENGTH_SHORT).show()
            finish()
        }

        showAnsBtn.setOnClickListener {
            // show answer instead of question
            cardText.text = flashCardList[currentCardIndex].answer
            // hide this button and show feed back group
            showAnsBtn.visibility = android.view.View.GONE
            feedbackGroup.visibility = android.view.View.VISIBLE
        }

        correctBtn.setOnClickListener {
            //adds one and displays next buton
            correctAns++
            feedbackGroup.visibility = android.view.View.GONE
            nextBtn.visibility = android.view.View.VISIBLE
        }

        wrongBtn.setOnClickListener {
            //remove the buttons and keep only next button
            feedbackGroup.visibility = android.view.View.GONE
            nextBtn.visibility = android.view.View.VISIBLE
        }

        nextBtn.setOnClickListener {
            currentCardIndex++ // moves to the next card index
            loadNewQuestion() //calls again the display the question function
        }
    }
}