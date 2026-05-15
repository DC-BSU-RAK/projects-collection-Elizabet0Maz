package com.example.memo_cards

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.nio.InvalidMarkException


class HomeScreenActivity : AppCompatActivity() {

    private lateinit var nameDisplay: TextView
    private lateinit var profileImage: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var settingsBtn: ImageView

    private var newGroupImageUri: String = ""

    private lateinit var popupImageHolder: ImageView

    private val pickCardImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {

            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flag)

            newGroupImageUri = uri.toString()
            popupImageHolder.setImageURI(uri)
            popupImageHolder.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    lateinit var data: ArrayList<GroupCards>
    lateinit var adapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        nameDisplay = findViewById(R.id.userNameDisplay)
        profileImage = findViewById(R.id.image_basic)
        settingsBtn = findViewById(R.id.settings)


        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedName = sharedPreferences.getString("userName", "")
        val savedImageString = sharedPreferences.getString("imageUri", "")

        nameDisplay.text = savedName


        //function for the pop-up window for help
        val helpBtn: ImageView = findViewById(R.id.help)

        helpBtn.setOnClickListener {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.help, null)
            val instructWindow = PopupWindow(popupView, 1000, 2000, true)
            instructWindow.showAtLocation(popupView, Gravity.CENTER, 10, 100)


            val closeButton : ImageView = popupView.findViewById(R.id.close)
            closeButton.setOnClickListener{
                instructWindow.dismiss()
            }

        }

        fun loadProfilePicture() {
            if (!savedImageString.isNullOrEmpty()) {
                val imageUri = Uri.parse(savedImageString)
                profileImage.setImageURI(imageUri)
                profileImage.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        //toast command
        fun showToast(message: String) {
            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
        }

        //call the loadProfilePicture function
        loadProfilePicture()
        //define the value for sharedPreferences edit
        val clearData = sharedPreferences.edit()

        settingsBtn.setOnClickListener {

            //this is made in order to not avoid loop
            //removes the data from userName and imageUri
            clearData.remove("userName")
            clearData.remove("imageUri")

            //applies the changes
            clearData.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //code taken from the same reference: https://www.geeksforgeeks.org/kotlin/android-recyclerview-in-kotlin/
        //here I have removed the for loop because it was used purely for testing this new concept

        //updated most of the code in order to use Gson to make sure that cards get saved in shared preference as array
        //reference: https://www.geeksforgeeks.org/kotlin/android-save-arraylist-to-sharedpreferences-with-kotlin/
        val recyclerview: RecyclerView = findViewById(R.id.groupRecyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)

        // loads arraylist from shared prefs
        //value for gson
        val gson = Gson()
        // getting to string present from shared prefs if not present set it as null or none
        val json = sharedPreferences.getString("group_cards", null)
        // gets the type of array list
        val type: Type = object : TypeToken<ArrayList<GroupCards>>() {}.type


        //slightly rewrote this part as compared to my reference because otherwise application would crash
        // checking below if the array list is empty or not
        if (json != null) {
            // getting data from gson and save it to array list
            data = gson.fromJson<Any>(json, type) as ArrayList<GroupCards>
        } else {
            data = ArrayList()
        }

        //for (i in 1..20) {
        //data.add(GroupCards(R.drawable.introimg.toString(), "Item $i"))
        //}
        adapter = GroupAdapter(data)
        recyclerview.adapter = adapter

//function for the pop-up window when adding a new card and the coordinates of its placement
        val instructButton: ImageView = findViewById(R.id.plus)
        instructButton.setOnClickListener {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_group, null)
            val instructWindow = PopupWindow(popupView, 1000, 2000, true)
            instructWindow.showAtLocation(popupView, Gravity.CENTER, 10, 100)

            val titleInput = popupView.findViewById<EditText>(R.id.titleCard)
            val imageInput: ImageView = popupView.findViewById(R.id.imageCard)
            popupImageHolder = imageInput
            imageInput.setOnClickListener {
                pickCardImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

//save button forces the pop-up to close
            val closeButton: ImageView = popupView.findViewById(R.id.save)
            closeButton.setOnClickListener {

                    //this part was challenging thankfully I found great reference that helped me understand it better
                    //https://suragch.medium.com/updating-data-in-an-android-recyclerview-842e56adbfd8
                    //inserts the list to add new unput data
                    val inputText = titleInput.text.toString()

                    //if statement to check if both inputs are there
                    if (inputText.isNotEmpty() && newGroupImageUri.isNotEmpty()) {

                        // creates a new array of user inputs
                        val newItems = arrayListOf(GroupCards(newGroupImageUri, inputText))

                        // stores value of the index
                        val insertIndex = 0

                        // inserts the new group at the very top of the list
                        data.addAll(insertIndex, newItems)

                        //notifies the adapter about the inserted item range
                        adapter.notifyItemRangeInserted(insertIndex, newItems.size)

                        //saving logic
                        // value for editor to store data in shared preferences
                        val editor = sharedPreferences.edit()
                        val gson = Gson()

                        // getting data from gson and storing it in a string
                        val jsonToSave: String = gson.toJson(data)

                        // Save it to memory
                        editor.putString("group_cards", jsonToSave)
                        editor.apply()

                        // close the window and reset
                        newGroupImageUri = ""
                        instructWindow.dismiss()

                    } else {
                        showToast("Please pick a photo and title")
                    }
                }
            }
        }
    }
