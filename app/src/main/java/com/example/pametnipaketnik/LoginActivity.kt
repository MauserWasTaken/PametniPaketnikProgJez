package com.example.pametnipaketnik

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var captureButton: Button
    private val client = OkHttpClient()
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100
    private val IMAGE_CAPTURE_COUNT = 1 // Only one image needed for login
    private var capturedImages: MutableList<Bitmap> = mutableListOf()
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imageView = findViewById(R.id.imageView)
        captureButton = findViewById(R.id.captureButton)

        // Get the username from intent data
        val data: Uri? = intent.data
        username = data?.getQueryParameter("username") ?: ""
        Log.d("LoginActivity", "Received username: $username") // Debug statement
        if (username.isEmpty()) {
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                dispatchTakePictureIntent()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            capturedImages.add(imageBitmap)
            imageView.setImageBitmap(imageBitmap)

            if (capturedImages.size < IMAGE_CAPTURE_COUNT) {
                // Capture the next image
                dispatchTakePictureIntent()
            } else {
                // All images captured, now upload them
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        requestBodyBuilder.addFormDataPart("username", username) // Ensure username is added here
        Log.d("LoginActivity", "Uploading image for username: $username") // Debug statement

        capturedImages.forEachIndexed { index, bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            requestBodyBuilder.addFormDataPart("image$index", "face$index.png", RequestBody.create("image/png".toMediaTypeOrNull(), byteArray))
        }

        val requestBody = requestBodyBuilder.build()
        val request = Request.Builder()
            .url("http://192.168.0.33:3001/users/verifyFaceImage")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.i("tag", "Failed to upload image: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Failed to upload image: ${response.message}", Toast.LENGTH_SHORT).show()
                        Log.i("tag", "Failed to upload image: ${response.message}")
                    }
                }
            }
        })
    }
}
