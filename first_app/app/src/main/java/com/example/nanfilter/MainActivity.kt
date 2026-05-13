package com.example.nanfilter

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    //reference for image handler: https://developer.android.com/training/data-storage/shared/photo-picker
    //all references used for this project will be available at the document in harvard style
    val pickingMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // photo picker
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")

            //pastes into imageHolder
            val imgHolder = findViewById<ImageView>(R.id.imageHolder)
            imgHolder.setImageURI(uri)
            imgHolder.scaleType = ImageView.ScaleType.CENTER_CROP

        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    val saturationHolder = android.graphics.ColorMatrix()
    val tintHolder = android.graphics.ColorMatrix()
    val brightnessHolder = android.graphics.ColorMatrix()
    val contrastHolder = android.graphics.ColorMatrix()

    var hasContrast = false
    var hasSaturation = false
    var hasTint = false
    var hasLight = false

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
        val Frame = findViewById<ImageView>(R.id.frame)

        // When the frame is clicked the function gets activated
        Frame.setOnClickListener {
            //launches the photo picker and allow the user choose only images
            pickingMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //Saturation

        //finds the image view for saturation image
        val saturationBottle = findViewById<ImageView>(R.id.saturation)

        //sets the Image for saturation bottle
        saturationBottle.setImageResource(R.drawable.bottle)

        saturationBottle.setOnClickListener {
            saturationBottle.setImageResource(R.drawable.glow_bottle) //changes the image

            //animates the Bottle
            val bottleAnim = AnimationUtils.loadAnimation(this, R.anim.move)
            saturationBottle.startAnimation(bottleAnim)

            //creates a delay to execute the popup screen
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({

                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup, null)
                val instructWindow = PopupWindow(popupView, 1100, 900, true)

                instructWindow.showAtLocation(popupView, Gravity.BOTTOM, 20, 90)

                // boolean that is used for identifying if user selected an option
                var saturationSelected = false

                // finds the image holder
                val imgHolder = findViewById<ImageView>(R.id.imageHolder)

                //four saturation buttons

                popupView.findViewById<ImageView>(R.id.lowSat).setOnClickListener {
                    saturationHolder.setSaturation(0.2f) //changes the saturation intensity
                    //hold the concatenated values for colour matrix
                    val mixture = android.graphics.ColorMatrix()

                    // Mixes the saturation with other filters
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(contrastHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(tintHolder)

                    //Applies the mixed colour matrix
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    saturationSelected = true
                    hasSaturation = true
                }

                popupView.findViewById<ImageView>(R.id.midSat).setOnClickListener {

                    val mixture = android.graphics.ColorMatrix()
                    saturationHolder.setSaturation(1.5f)
                    // Mixes the saturation with other filters
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    mixture.postConcat(tintHolder)

                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    saturationSelected = true
                    hasSaturation = true
                }

                popupView.findViewById<ImageView>(R.id.highSat).setOnClickListener {


                    val mixture = android.graphics.ColorMatrix()

                    saturationHolder.setSaturation(2.5f)
                    // Mixes the saturation with other filters
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(contrastHolder)

                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    saturationSelected = true
                    hasSaturation = true
                }

                popupView.findViewById<ImageView>(R.id.noneSat).setOnClickListener {
                    saturationHolder.setSaturation(1f)

                    val mixture = android.graphics.ColorMatrix()

                    // Mixes the saturation with other filters
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(contrastHolder)

                    mixture.postConcat(brightnessHolder)

                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    saturationSelected = true
                    hasSaturation = true
                }

                //close button
                val closeButton: ImageView = popupView.findViewById(R.id.closeButtonSaturation)
                closeButton.setOnClickListener {
                    //Checks if option was selected before closing
                    if (saturationSelected == true) {
                        instructWindow.dismiss()
                        saturationBottle.setImageResource(R.drawable.bottle)
                    } else {
                        // Toast message
                        android.widget.Toast.makeText(
                            this,
                            "The Queen demands you pick a potion first!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }, 1000)
        }


        //Tint section

        val tintColours = findViewById<ImageView>(R.id.tint)

        tintColours.setImageResource(R.drawable.colours)

        tintColours.setOnClickListener {
            tintColours.setImageResource(R.drawable.colours_glow)

            val coloursAnim = AnimationUtils.loadAnimation(this, R.anim.move)
            tintColours.startAnimation(coloursAnim)

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({

                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_tint, null)
                val instructWindow = PopupWindow(popupView, 1100, 900, true)

                instructWindow.showAtLocation(popupView, Gravity.BOTTOM, 20, 90)

                // finds the image holder
                val imgHolder = findViewById<ImageView>(R.id.imageHolder)

                // boolean that is used for identifying if user selected an option
                var tintSelected = false

                popupView.findViewById<ImageView>(R.id.red).setOnClickListener {


                    tintHolder.set(
                        floatArrayOf(
                            1.5f, 0f, 0f, 0f, 0f,   // Red Row
                            0f, 1f, 0f, 0f, 0f,  // Green Row
                            0f, 0f, 1f, 0f, 0f,  // Blue Row
                            0f, 0f, 0f, 1f, 0f   // Alpha Row
                        )
                    )
                    val mixture = android.graphics.ColorMatrix()
                    // Mixes the saturation with other filters
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    tintSelected = true
                    hasTint = true
                }

                popupView.findViewById<ImageView>(R.id.blue).setOnClickListener {
                    tintHolder.set(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,   // Red Row
                            0f, 1f, 0f, 0f, 0f,  // Green Row
                            0f, 0f, 1.5f, 0f, 0f,  // Blue Row
                            0f, 0f, 0f, 1f, 0f   // Alpha Row
                        )
                    )
                    val mixture = android.graphics.ColorMatrix()
                    // Mixes the saturation with other filters
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(contrastHolder)
                    mixture.postConcat(brightnessHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    tintSelected = true
                    hasTint = true
                }

                popupView.findViewById<ImageView>(R.id.green).setOnClickListener {
                    tintHolder.set(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,   // Red Row
                            0f, 1.5f, 0f, 0f, 0f,  // Green Row
                            0f, 0f, 1f, 0f, 0f,  // Blue Row
                            0f, 0f, 0f, 1f, 0f   // Alpha Row
                        )
                    )
                    val mixture = android.graphics.ColorMatrix()
                    // Mixes the saturation with other filters
                    mixture.postConcat(contrastHolder)
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    tintSelected = true
                    hasTint = true
                }

                popupView.findViewById<ImageView>(R.id.yellow).setOnClickListener {
                    tintHolder.set(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            0f, 0f, 0.3f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                    val mixture = android.graphics.ColorMatrix()
                    // Mixes the saturation with other filters
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    tintSelected = true
                    hasTint = true
                }

                popupView.findViewById<ImageView>(R.id.white).setOnClickListener {
                    tintHolder.set(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            0f, 0f, 1f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                    val mixture = android.graphics.ColorMatrix()
                    // Mixes the saturation with other filters
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(contrastHolder)
                    mixture.postConcat(brightnessHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    tintSelected = true
                    hasTint = true
                }

                val closeButton: ImageView = popupView.findViewById(R.id.closeButtonTint)
                closeButton.setOnClickListener {
                    //Checks if option was selected before closing
                    if (tintSelected == true) {
                        instructWindow.dismiss()
                        tintColours.setImageResource(R.drawable.colours)
                    } else {
                        // Toast message
                        android.widget.Toast.makeText(
                            this,
                            "You must take at least ONE candy before you leave!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }, 1000)
        }

        val Brightness = findViewById<ImageView>(R.id.lightBulb)

        //sets the Image for saturation bottle
        Brightness.setImageResource(R.drawable.no_light)

        Brightness.setOnClickListener {
            Brightness.setImageResource(R.drawable.bright_light)

            val coloursAnim = AnimationUtils.loadAnimation(this, R.anim.move)
            Brightness.startAnimation(coloursAnim)

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({

                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_brightness, null)
                val instructWindow = PopupWindow(popupView, 1100, 900, true)

                instructWindow.showAtLocation(popupView, Gravity.BOTTOM, 20, 90)

                val imgHolder = findViewById<ImageView>(R.id.imageHolder)
                var lightSelected = false

                popupView.findViewById<ImageView>(R.id.noLight).setOnClickListener {
                    brightnessHolder.set(floatArrayOf(
                        1f, 0f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f, 0f,
                        0f, 0f, 1f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    ))
                    val mixture = android.graphics.ColorMatrix()
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    lightSelected = true
                    hasLight = true
                }

                popupView.findViewById<ImageView>(R.id.lowLight).setOnClickListener {
                    brightnessHolder.set(floatArrayOf(
                        1f, 0f, 0f, 0f, -50f,
                        0f, 1f, 0f, 0f, -50f,
                        0f, 0f, 1f, 0f, -50f,
                        0f, 0f, 0f, 1f, 0f
                    ))
                    val mixture = android.graphics.ColorMatrix()
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    lightSelected = true
                    hasLight = true
                }

                popupView.findViewById<ImageView>(R.id.midLight).setOnClickListener {
                    brightnessHolder.set(floatArrayOf(
                        1f, 0f, 0f, 0f, 40f,
                        0f, 1f, 0f, 0f, 40f,
                        0f, 0f, 1f, 0f, 40f,
                        0f, 0f, 0f, 1f, 0f
                    ))
                    val mixture = android.graphics.ColorMatrix()
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    lightSelected = true
                    hasLight = true
                }

                popupView.findViewById<ImageView>(R.id.brightLight).setOnClickListener {
                    brightnessHolder.set(floatArrayOf(
                        1f, 0f, 0f, 0f, 80f,
                        0f, 1f, 0f, 0f, 80f,
                        0f, 0f, 1f, 0f, 80f,
                        0f, 0f, 0f, 1f, 0f
                    ))
                    val mixture = android.graphics.ColorMatrix()
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    lightSelected = true
                    hasLight = true
                }

                val closeButton: ImageView = popupView.findViewById(R.id.closeButtonLight)
                closeButton.setOnClickListener {
                    if (lightSelected == true) {
                        instructWindow.dismiss()
                        Brightness.setImageResource(R.drawable.no_light)
                    } else {
                        android.widget.Toast.makeText(this@MainActivity, "Do NOT forget to switch off or on the light! :)", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

            }, 1000)
        }

        val Contrast = findViewById<ImageView>(R.id.contrastCards)

        //sets the Image for saturation bottle
        Contrast.setImageResource(R.drawable.cards)

        Contrast.setOnClickListener {
           Contrast.setImageResource(R.drawable.cards_glow)

            val coloursAnim = AnimationUtils.loadAnimation(this, R.anim.move)
            Contrast.startAnimation(coloursAnim)

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({

                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_contrast, null)
                val instructWindow = PopupWindow(popupView, 1100, 900, true)

                instructWindow.showAtLocation(popupView, Gravity.BOTTOM, 20, 90)

                val imgHolder = findViewById<ImageView>(R.id.imageHolder)
                var contrastSelected = false

                popupView.findViewById<ImageView>(R.id.highContrast).setOnClickListener {
                    contrastHolder.set(floatArrayOf(
                        1.5f, 0f, 0f, 0f, -64f,
                        0f, 1.5f, 0f, 0f, -64f,
                        0f, 0f, 1.5f, 0f, -64f,
                        0f, 0f, 0f, 1f, 0f
                    )
                    )
                    val mixture = android.graphics.ColorMatrix()
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    contrastSelected = true
                    hasContrast = true
                }

                popupView.findViewById<ImageView>(R.id.midContrast).setOnClickListener {
                    contrastHolder.set(floatArrayOf(
                        1.25f, 0f, 0f, 0f, -32f,
                        0f, 1.25f, 0f, 0f, -32f,
                        0f, 0f, 1.25f, 0f, -32f,
                        0f, 0f, 0f, 1f, 0f
                    ))
                    val mixture = android.graphics.ColorMatrix()
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    contrastSelected = true
                    hasContrast = true
                }

                popupView.findViewById<ImageView>(R.id.lowContrast).setOnClickListener {
                    contrastHolder.set(floatArrayOf(
                        0.5f, 0f, 0f, 0f, 64f,
                        0f, 0.5f, 0f, 0f, 64f,
                        0f, 0f, 0.5f, 0f, 64f,
                        0f, 0f, 0f, 1f, 0f
                    )
                    )
                    val mixture = android.graphics.ColorMatrix()
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    contrastSelected = true
                    hasContrast = true
                }

                popupView.findViewById<ImageView>(R.id.noContrast).setOnClickListener {
                    contrastHolder.set(floatArrayOf(
                        1f, 0f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f, 0f,
                        0f, 0f, 1f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                    )
                    val mixture = android.graphics.ColorMatrix()
                    mixture.postConcat(saturationHolder)
                    mixture.postConcat(tintHolder)
                    mixture.postConcat(brightnessHolder)
                    mixture.postConcat(contrastHolder)
                    imgHolder.colorFilter = android.graphics.ColorMatrixColorFilter(mixture)
                    contrastSelected = true
                    hasContrast = true
                }

                val closeButton: ImageView = popupView.findViewById(R.id.closeButtonContrast)
                closeButton.setOnClickListener {
                    if (contrastSelected == true) {
                        instructWindow.dismiss()
                        Contrast.setImageResource(R.drawable.cards)
                    } else {
                        android.widget.Toast.makeText(this@MainActivity, "Do NOT forget to switch off or on the light! :)", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

            }, 1000)
        }

        val helpButton = findViewById<ImageView>(R.id.help)

        helpButton.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this,R.anim.rotate)
            helpButton.startAnimation(animation)

                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_help,null)
                val instructWindow = PopupWindow(popupView, 1000,2000,true)
                instructWindow.showAtLocation(popupView,Gravity.CENTER,20,100)

                val closeButton : ImageView = popupView.findViewById(R.id.closeButtonHelp)
                closeButton.setOnClickListener {
                    instructWindow.dismiss()
                }
            }


        // Initialize MediaPlayer variable
        val mediaPlayer = MediaPlayer.create(this, R.raw.piano)

        // Initialize a button
        val musicButton: ImageView = findViewById(R.id.soundButton)

        // Check if player is loaded
        if (mediaPlayer == null) {
            android.widget.Toast.makeText(this, "Failed to initialize media player", android.widget.Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mediaPlayer.start() //Begins the music specifically when application only opens

        musicButton.setOnClickListener {
            // Checks if the music is playing at the moment
            if (mediaPlayer.isPlaying) {
                // stops playing music
                mediaPlayer.pause()
                mediaPlayer.seekTo(0) // Rewinds to the beginning

                // changes the image
                musicButton.setImageResource(R.drawable.no_sound)


            } else {
                mediaPlayer.start() //starts music again after the button is clicked
                //Changes the image
                musicButton.setImageResource(R.drawable.sound)

            }
        }



        //finds the views to capture an image
        val viewToCapture = findViewById<View>(R.id.imageHolder)
        val saveButton = findViewById<ImageView>(R.id.save)

        saveButton.setOnClickListener {
            //checks if all variables are true
            if (hasSaturation && hasTint && hasLight && hasContrast) {

                //I would like to reference this video which helped me a lot writing code for saving images without it
                // Mission felt impossible
                // reference: https://youtu.be/AuID5KSYXgQ?si=UQC1GH70gYRvwFsm

                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

                // takes screenshot of the Imageview
                var bitmap: Bitmap? = null
                try {
                    bitmap = Bitmap.createBitmap(viewToCapture.measuredWidth, viewToCapture.measuredHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    viewToCapture.draw(canvas)
                } catch (e: Exception) {
                    Log.e("Didn't work", "Cannot Capture")
                }

                // saves images depending on the android version
                bitmap?.let { btm ->
                    val imageName = "WonderImg_${System.currentTimeMillis()}.jpg"
                    var fos: OutputStream? = null

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // new Android
                        this.contentResolver?.also { resolver ->
                            val contentValues = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                            }
                            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                            fos = imageUri?.let { resolver.openOutputStream(it) }
                        }
                    } else {
                        // older Android
                        val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        val imageFile = File(imagesDirectory, imageName)
                        fos = FileOutputStream(imageFile)
                    }

                    // compresses the bitmap
                    fos?.use { stream ->
                        btm.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        Toast.makeText(this, "Successfully Captured the Image!", Toast.LENGTH_LONG).show()

                        // Restarts the application
                        val restartIntent = android.content.Intent(this, MainActivity::class.java)
                        startActivity(restartIntent)
                        finish()
                    }
                }

            } else {
                //missing filters
                android.widget.Toast.makeText(this, "Wait! You must get all the filters first!!!", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
}
