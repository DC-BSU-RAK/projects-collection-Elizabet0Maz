package com.example.memo_cards

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter(private val list: List<GroupCards>) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    //code is completely inspired from: https://www.geeksforgeeks.org/kotlin/android-recyclerview-in-kotlin/
    //and adjusted to be compatible with  my application.
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // loadds the flash_group view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.flash_group, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.textView.text = item.text

        // Sets up the image
        holder.imageView.setImageURI(Uri.parse(item.image))
        holder.imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        // Links a value to a specific row layout which is held by the adapter
        val cardRow: View = holder.itemView
        cardRow.setOnClickListener {

            val context = cardRow.context

            // Open the shared pref and save which group got clicked
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("clickedTitle", item.text)
            editor.putString("clickedImg", item.image)
            editor.apply()

            // opens the FlashCardsPageActivity
            val intent = Intent(context, FlashCardsPageActivity::class.java)
            context.startActivity(intent)
        }
    }


        // return the number of the items in the list
        override fun getItemCount(): Int {
            return list.size
        }

        // Holds the views for adding it to image and text
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = itemView.findViewById(R.id.imageGroup)
            val textView: TextView = itemView.findViewById(R.id.title_group)
        }
}
