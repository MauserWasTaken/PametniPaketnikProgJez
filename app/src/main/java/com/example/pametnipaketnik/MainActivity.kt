package com.example.pametnipaketnik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Poišči gumb po ID-ju
        val qrScannerButton: Button = findViewById(R.id.qrScannerButton)

        // Nastavi OnClickListener za gumb
        qrScannerButton.setOnClickListener {
            startQRScanner()
        }
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
            Toast.makeText(this, "Scanned: ${result.contents}", Toast.LENGTH_LONG).show()
        } else {
            // QR koda ni skenirana
            Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
        }
    }
}
