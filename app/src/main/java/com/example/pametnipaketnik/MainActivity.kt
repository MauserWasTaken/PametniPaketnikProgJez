package com.example.pametnipaketnik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var scanCounterTextView: TextView  // TextView za prikaz števca

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val app = application as MyApplication

        // Poišči gumb in TextView po ID-ju
        val qrScannerButton: Button = findViewById(R.id.qrScannerButton)
        scanCounterTextView = findViewById(R.id.scanCounterTextView)

        // Nastavi OnClickListener za gumb
        qrScannerButton.setOnClickListener {
            startQRScanner()
        }

        // Prikaži trenutni števec ob zagonu
        updateScanCounter()
    }

    // Funkcija za zagon QR skenerja
    private fun startQRScanner() {
        val options = ScanOptions()
        options.setPrompt("Scan a QR code")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        qrCodeLauncher.launch(options)
    }

    private val qrCodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            // QR koda uspešno skenirana
            val app = application as MyApplication
            app.scanCounter++  // Povečaj števec

            // Ustvari nov objekt Opened
            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val opened = Opened(openedTime = currentTime)

            // Dodaj objekt Opened v seznam
            app.OpenedList.add(opened)

            Log.d("TAG", "OpenedList: ${app.OpenedList.joinToString(separator = "\n") { it.toString() }}")


            updateScanCounter()  // Posodobi TextView
            Toast.makeText(this, "Scanned: ${result.contents}\nCounter: ${app.scanCounter}", Toast.LENGTH_LONG).show()
        } else {
            // QR koda ni skenirana
            Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateScanCounter() {
        val app = application as MyApplication
        scanCounterTextView.text = "Scans: ${app.scanCounter}"
    }
}
