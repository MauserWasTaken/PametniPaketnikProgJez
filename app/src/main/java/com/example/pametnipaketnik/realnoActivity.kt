package com.example.pametnipaketnik

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast

class RealnoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OpenedItemAdapter
    private lateinit var openedItemList: MutableList<City>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acttivity_realno)

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.openedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Access the OpenedList from MyApplication
        openedItemList = (applicationContext as MyApplication).CityList

        // Initialize the adapter
        adapter = OpenedItemAdapter(openedItemList)
        recyclerView.adapter = adapter

        // Example: Display a Toast when the activity is created
        Toast.makeText(this, "List initialized with ${openedItemList.size} items", Toast.LENGTH_SHORT).show()
    }

    // Data model for Opened
    data class Opened(val name: String, val xCoordinate: Int, val yCoordinate: Int)

    // Adapter for RecyclerView
    class OpenedItemAdapter(private val itemList: MutableList<City>) : RecyclerView.Adapter<OpenedItemAdapter.OpenedItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenedItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.opened_item, parent, false)
            return OpenedItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: OpenedItemViewHolder, position: Int) {
            val item = itemList[position]
            holder.nameTextView.text = item.name
            holder.xCoordinateTextView.text = "X: ${item.x}"
            holder.yCoordinateTextView.text = "Y: ${item.y}"
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        inner class OpenedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
            val xCoordinateTextView: TextView = itemView.findViewById(R.id.xCoordinateTextView)
            val yCoordinateTextView: TextView = itemView.findViewById(R.id.yCoordinateTextView)
        }
    }
}
