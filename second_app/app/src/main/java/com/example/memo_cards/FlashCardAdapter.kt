package com.example.memo_cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//code referenced from: https://www.geeksforgeeks.org/kotlin/android-recyclerview-in-kotlin/
class FlashCardAdapter(private val list: List<FlashCard>) : RecyclerView.Adapter<FlashCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.flash_card, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.questionView.text = item.question
        holder.answerView.text = item.answer
    }

    //return the number of the items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    //behaves like a container that stores and links the code to the ids in flash_card xml
    //preventing the app from repeatedly searching for that specific xml file
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionView: TextView = view.findViewById(R.id.questionField)
        val answerView: TextView = view.findViewById(R.id.answerField)
    }
}