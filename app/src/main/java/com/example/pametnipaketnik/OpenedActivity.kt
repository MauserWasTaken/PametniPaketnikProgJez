package com.example.pametnipaketnik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import java.text.SimpleDateFormat
import java.util.*


class OpenedListActivity : AppCompatActivity() {

    private lateinit var openedAdapter: OpenedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opened_list)



        val openedRecyclerView = findViewById<View>(R.id.openedRecyclerView) as RecyclerView
        // Nastavi RecyclerView
        openedRecyclerView.layoutManager = LinearLayoutManager(this)
        openedAdapter = OpenedAdapter((application as MyApplication).OpenedList)
        openedRecyclerView.adapter = openedAdapter
    }
}
