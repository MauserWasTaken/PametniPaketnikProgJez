package com.example.pametnipaketnik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Poišči gumb po ID-ju
        val qrScannerButton: Button = findViewById(R.id.qrScannerButton)

        // Nastavi OnClickListener za gumb
        qrScannerButton.setOnClickListener {
            Toast.makeText(this, "Odpiranje QR scannerja", Toast.LENGTH_SHORT).show()
        }
    }
}
