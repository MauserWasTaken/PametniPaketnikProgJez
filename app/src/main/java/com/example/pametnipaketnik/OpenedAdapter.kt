package com.example.pametnipaketnik

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OpenedAdapter(private val openedList: List<Opened>) : RecyclerView.Adapter<OpenedAdapter.OpenedViewHolder>() {

    class OpenedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val openedTimeTextView: TextView = itemView.findViewById(R.id.openedTimeTextView)
        val openedPackageTextView: TextView = itemView.findViewById(R.id.openedPackageTextView)
        val openedLocationTextView: TextView = itemView.findViewById(R.id.openedLocationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenedViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.opened_item, parent, false)
        return OpenedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OpenedViewHolder, position: Int) {
        val currentItem = openedList[position]
        holder.openedTimeTextView.text = currentItem.openedTime
        holder.openedPackageTextView.text = currentItem.openedPackage
        holder.openedLocationTextView.text = currentItem.openedLocation
    }

    override fun getItemCount() = openedList.size
}
