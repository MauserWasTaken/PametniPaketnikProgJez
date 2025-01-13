package com.example.pametnipaketnik

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import projekt.GA
import projekt.TSP

class TestActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)

        val startButton = findViewById<Button>(R.id.start_button)
        val canvasView = findViewById<TourCanvasView>(R.id.canvas_view)

        val coroutineScope = lifecycleScope

        startButton.setOnClickListener {
            val test: AdressReader= AdressReader()

//            coroutineScope.launch(Dispatchers.Default) {
//                test.Testread(this@TestActivity)
//            }

            coroutineScope.launch(Dispatchers.Default) {
                val eilTsp = TSP("realWorldProblemDistance.tsp", 1000,this@TestActivity)
                val ga = GA(100, 0.8, 0.1) { canvasView.setTour(it) }

                val bestPath = ga.execute(eilTsp)
                withContext(Dispatchers.Main) {
                    canvasView.setTour(bestPath)
                    Log.i("konec","konec")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Ustavi vse coroutine ob uničenju aktivnosti
    }
}
