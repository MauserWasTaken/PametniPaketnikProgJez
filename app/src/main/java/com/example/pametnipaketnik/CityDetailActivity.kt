package com.example.pametnipaketnik

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class CityDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_detail)

        val name = intent.getStringExtra("name")
        val x = intent.getIntExtra("x", 0)
        val y = intent.getIntExtra("y", 0)

        Log.d("CityDetailActivity", "Name: $name, X: $x, Y: $y")
    }
}
