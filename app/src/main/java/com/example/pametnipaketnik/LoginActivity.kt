package com.example.pametnipaketnik

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Handle the intent
        val intent = intent
        val action = intent.action
        val data = intent.dataString

        // Display a toast message with the intent data
        Toast.makeText(this, "Login Activity Launched with data: $data", Toast.LENGTH_SHORT).show()

        // Implement your login logic here
    }
}