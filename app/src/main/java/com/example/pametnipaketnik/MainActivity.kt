package com.example.pametnipaketnik

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import weka.core.Instances
import weka.core.converters.ArffLoader
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import kotlin.math.log
import kotlin.math.pow


class MainActivity : AppCompatActivity() {

    private lateinit var scanCounterTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val app = application as MyApplication

        val qrScannerButton: Button = findViewById(R.id.qrScannerButton)
        val openedListButton: Button = findViewById(R.id.buttonList)
        val testButton: Button = findViewById(R.id.test)
        val realnoButton: Button = findViewById(R.id.buttonReal)
        scanCounterTextView = findViewById(R.id.scanCounterTextView)

        qrScannerButton.setOnClickListener {
            startQRScanner()
        }

        openedListButton.setOnClickListener {
            Log.i("neki", "tu esm")
            val intent = Intent(this, OpenedListActivity::class.java)
            startActivity(intent)
        }

        testButton.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }

        realnoButton.setOnClickListener {


            val userLat = 44.0  // Replace with actual user latitude
            val userLon = 13.0  // Replace with actual user longitude

            // Get 5 closest packages
            getFiveClosestPackages(userLat, userLon)


            val intent = Intent(this, RealnoActivity::class.java)
            startActivity(intent)
        }

        updateScanCounter()
    }


    fun loadDataFromAssets(context: Context): Instances {
        // Pridobite InputStream za .arff datoteko v assets
        val assetManager = context.assets
        val inputStream: InputStream = assetManager.open("suinGeneratedData.arff")

        // Ustvarite ArffLoader in naložite podatke iz InputStream
        val arffLoader = ArffLoader()
        arffLoader.setSource(inputStream)
        return arffLoader.getDataSet()
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // Haversine formula to calculate the distance between two lat/lon points
        val earthRadius = 6371.0  // km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2).pow(2.0) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2).pow(2.0)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }

    fun getFiveClosestPackages(userLat: Double, userLon: Double) {
        val instances = loadDataFromAssets(applicationContext)
        val distances = mutableListOf<Pair<Double, String>>()  // Store distance and ID

        for (i in 0 until instances.numInstances()) {
            val instance = instances.instance(i)
            val lat = instance.value(instances.attribute("Geolokacija_lat"))
            val lon = instance.value(instances.attribute("Geolokacija_lon"))

            val distance = calculateDistance(userLat, userLon, lat, lon)
            val packageId = instance.stringValue(instances.attribute("Vrsta_paketnika"))

            distances.add(Pair(distance, "$packageId,$lat,$lon"))
        }

        // Sort the distances in ascending order (closest first)
        val sortedDistances = distances.sortedBy { it.first }

        // Take the top 5 closest
        val closestPackages = sortedDistances.take(5)

        // Adding closest packages to packageList
        val app = application as MyApplication
        app.packageList.clear()
        var i=0
        closestPackages.forEach {
            i++
            val packageData = it.second.split(",")
            val id = i
            val lat = packageData[1].toDouble()
            val lon = packageData[2].toDouble()
            app.packageList.add(Package(id.toString(), lat, lon))  // Assuming Package class has the constructor (id, lat, lon)
        }

        // Log the result for debugging
        closestPackages.forEach {
            Log.i("ClosestPackage", it.second)
        }
    }





    private fun startQRScanner() {
        val options = ScanOptions()
        options.setPrompt("Scan a QR code")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        qrCodeLauncher.launch(options)
    }

    private val qrCodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            Log.i("neki", "tu esm3")
            val app = application as MyApplication

            Log.d("TAG", "OpenedList: ${app.OpenedList.joinToString(separator = "\n") { it.toString() }}")

            updateScanCounter()

            //val boxId = 537
            val qrCodeInfo = result.contents  // Pridobimo ID paketnika iz QR kode

            val parts = qrCodeInfo.split('/') // Razdelite QR kodo glede na znak '/'
            val boxIdIndex = parts.indexOf("000537") // Indeks, kjer se nahaja ID paketnika

            val boxId = parts[boxIdIndex].toInt() // ID paketnika



            Log.i("qrInfo", qrCodeInfo)
            Log.i("boxID", boxId.toString())

            val tokenFormat = 2  // Nastavimo format žetona

            // Pokličemo funkcijo za odpiranje paketnika
            openBox(boxId, tokenFormat, qrCodeInfo)

            app.scanCounter++

            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val opened = Opened(openedTime = currentTime,boxId)
            app.OpenedList.add(opened)
            updateScanCounter()

        } else {
            Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateScanCounter() {
        val app = application as MyApplication
        scanCounterTextView.text = "Scans: ${app.scanCounter}"
    }

    private fun openBox(boxId: Int, tokenFormat: Int, qrCodeInfo: String) {
        val url = "https://api-d4me-stage.direct4.me/sandbox/v1/Access/openbox"
        val client = OkHttpClient()
        val json = JSONObject()
        json.put("deliveryId", 0)
        json.put("boxId", boxId)
        json.put("tokenFormat", tokenFormat)
        json.put("latitude", 0)  // Replace with actual latitude if necessary
        json.put("longitude", 0) // Replace with actual longitude if necessary
        json.put("qrCodeInfo", qrCodeInfo)  // Example value, replace with actual QR code info if necessary
        json.put("terminalSeed", 0)
        json.put("isMultibox", false)
        json.put("doorIndex", 0)
        json.put("addAccessLog", true)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer 9ea96945-3a37-4638-a5d4-22e89fbc998f")
            .addHeader("Content-Type", "application/json")
            .build()


        val buffer = okio.Buffer()
        body?.writeTo(buffer)
        val bodyContent = buffer.readUtf8()
        Log.i("telo", bodyContent)
        Log.i("prosnja", request.toString())

        CoroutineScope(Dispatchers.IO).launch {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("API", "Failed to make API call", e)
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "API call failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {

                    Log.i("odgovor",response.toString())
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            val jsonResponse = JSONObject(responseBody)
                            val data = jsonResponse.getString("data")
                            val result = jsonResponse.getString("result")
                            val errorNumber = jsonResponse.getInt("errorNumber")

                            Log.i("errornum",errorNumber.toString())
                            Log.i("body",responseBody)
                            Log.i("result",result)
                            Log.i("dato",data)

                            if (errorNumber == 0) {
                                decompresAndPlay(data)

                                if (data != "") {
                                    runOnUiThread {
                                        //playToken(data)
                                    }
                                } else {
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity, "Failed to unzip token", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "Error: $result", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Empty response body", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "API call unsuccessful", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }

    fun decompresAndPlay(getapi: String) {
        val decodedBytes = Base64.decode(getapi, Base64.DEFAULT)

        Log.i("decodedd",decodedBytes.toString())


        val tempFile = File.createTempFile("temp", ".wav", cacheDir)
        val fos = FileOutputStream(tempFile)
        fos.write(decodedBytes)
        val zipFile = ZipFile(tempFile)
        val entries = zipFile.entries()

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val entryName = entry.name
            val entryInputStream = zipFile.getInputStream(entry)

            val tempEntryFile = File.createTempFile("temp", null, cacheDir)
            val foss = FileOutputStream(tempEntryFile)
            entryInputStream.copyTo(foss)
            fos.close()

            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(tempEntryFile.absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()

            entryInputStream.close()

            finish()
        }

        finish()
    }

}
