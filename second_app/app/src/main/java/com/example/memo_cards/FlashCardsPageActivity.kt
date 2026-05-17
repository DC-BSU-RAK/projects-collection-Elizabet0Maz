package com.example.memo_cards

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class FlashCardsPageActivity : AppCompatActivity() {

    lateinit var flashCardList: ArrayList<FlashCard>
    lateinit var studyIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_flash_cards_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // integrating shared preferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        ///places the image and title for the group user selected
        // extracting the specific group card data which is saved
        val savedTitle = sharedPreferences.getString("clickedTitle", "")
        val savedImage = sharedPreferences.getString("clickedImg", "").toString()


        //updated most of the code in order to use Gson to make sure that cards get saved in shared preference
        // reference: https://www.geeksforgeeks.org/kotlin/android-save-arraylist-to-sharedpreferences-with-kotlin/
        val recyclerview: RecyclerView = findViewById(R.id.flashcardRecyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)
        //loads arraylist from shared prefs
        // value for gson
        val gson = Gson()

        // getting to string present from shared prefs if not present set it as null or none
        // I used savedTitle here so it saves cards for this specific group
        val json = sharedPreferences.getString("${savedTitle}_cards", null)

        // gets the type of array list
        val type: Type = object : TypeToken<ArrayList<FlashCard>>() {}.type

        //defines variablet that holds flashcard list
        //var flashCardList: ArrayList<FlashCard>
        //slightly rewrote this part as compared to my reference because otherwise application would crash
        // checking below if the array list is empty or not
        if (json != null) {
            // getting data from gson and save it to array list
            flashCardList = gson.fromJson(json, type)
        } else {
            flashCardList = ArrayList()
        }

        val adapter = FlashCardAdapter(flashCardList)
        recyclerview.adapter = adapter



        //defining the values for needed xml ids
        val groupNameDisplay = findViewById<TextView>(R.id.GroupName)
        val groupImageDisplay = findViewById<ImageView>(R.id.imageGroupInside)
        val backArrow = findViewById<ImageView>(R.id.backBtnCard)
        val plusButton = findViewById<ImageView>(R.id.addCardsBtn) //pop up


        //displaying this group card data and cropping the image
        groupNameDisplay.text = savedTitle

        groupImageDisplay.setImageURI(Uri.parse(savedImage))
        groupImageDisplay.scaleType = ImageView.ScaleType.CENTER_CROP

        //arrow back to go back to the homepage
        backArrow.setOnClickListener {
            finish()
        }

        //plus button popup for questions and answers
        plusButton.setOnClickListener {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_add_flashcards, null)
            val instructWindow = PopupWindow(popupView, 1000, 2000, true)
            instructWindow.showAtLocation(popupView, Gravity.CENTER, 10, 100)

            // finds the xml ids
            val questionInput = popupView.findViewById<EditText>(R.id.addQuestion)
            val answerInput = popupView.findViewById<EditText>(R.id.addAnswer)
            val saveButton = popupView.findViewById<ImageView>(R.id.saveAddCard)

            saveButton.setOnClickListener {
                //reference: https://suragch.medium.com/updating-data-in-an-android-recyclerview-842e56adbfd8
                //inserts the list to add new unput data
                val inputText = questionInput.text.toString()
                val inputAnswer = answerInput.text.toString()


                //if statement to check if both inputs are there
                if (inputText.isNotEmpty() && inputAnswer.isNotEmpty()) {

                    // creates a new array of user inputs
                    val newItems = arrayListOf(FlashCard(inputText, inputAnswer))
                    // stores value of the index
                    val insertIndex = 0
                    // inserts the new group at the very top of the list
                    flashCardList.addAll(insertIndex, newItems)
                    //notifies the adapter about the inserted item range
                    adapter.notifyItemRangeInserted(insertIndex, newItems.size)

                    //saving logic
                    // value for editor to store data in shared preferences
                    val editor = sharedPreferences.edit()
                    val gson = Gson()

                    // getting data from gson and storing it in a string
                    val jsonToSave: String = gson.toJson(flashCardList)

                    // Save it to memory
                    //using string template to save
                    editor.putString("${savedTitle}_cards", jsonToSave)
                    editor.apply()

                    // close the window and reset
                    instructWindow.dismiss()
                }
            }
        }

        //finds the button
        val startStudyBtn = findViewById<ImageView>(R.id.startLearn)
        //toast command
        fun showToast(message: String) {
            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
        }

        startStudyBtn.setOnClickListener {
            if (flashCardList.isEmpty()) {
                //calling showToast if = zero
                showToast("Add some cards first. . .")
            }
            else {

                //intent to switch to test flash cards
                studyIntent = Intent(this, TestFlashCardsActivity::class.java)
                //starts activity
                startActivity(studyIntent)
            }
        }
    }
}
