// CityAdapter.kt
package com.example.pametnipaketnik

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CityAdapter(
    private val context: Context,
    private val cityList: List<City>,
    private val onItemClick: (City) -> Unit // Sprejme funkcijo kot parameter
    ) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.city_layout, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cityList[position]
        holder.bind(city)
        // Nastavite klik poslušalca
        holder.itemView.setOnClickListener {
            onItemClick(city) // Pokliče funkcijo, ko se klikne element
        }
    }

    override fun getItemCount(): Int {
        return cityList.size
    }

    class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val xCoordinateTextView: TextView = itemView.findViewById(R.id.xCoordinateTextView)
        private val yCoordinateTextView: TextView = itemView.findViewById(R.id.yCoordinateTextView)

        fun bind(city: City) {
            nameTextView.text = city.name
            xCoordinateTextView.text = "X: ${city.x}"
            yCoordinateTextView.text = "Y: ${city.y}"

        }


    }
}