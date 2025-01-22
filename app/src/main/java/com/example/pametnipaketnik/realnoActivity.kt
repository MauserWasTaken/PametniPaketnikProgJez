package com.example.pametnipaketnik

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RealnoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acttivity_realno) // Popravljena tipkarska napaka v imenu postavitve

        // Pridobitev instance aplikacije
        val app = application as MyApplication
        val cityList = app.packageList

        // Nastavitev RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.openedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CityAdapter(this, cityList) { city ->
            // Odpri novo aktivnost ob kliku na element
            val intent = Intent(this, CityDetailActivity::class.java)
            intent.putExtra("name", city.name)
            intent.putExtra("x", city.x)
            intent.putExtra("y", city.y)
            startActivity(intent)
        }
    }
}
