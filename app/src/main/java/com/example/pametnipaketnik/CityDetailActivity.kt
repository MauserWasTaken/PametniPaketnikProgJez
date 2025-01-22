package com.example.pametnipaketnik

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.GeoPoint

class CityDetailActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance()
            .load(applicationContext, this.getPreferences(Context.MODE_PRIVATE))
        setContentView(R.layout.activity_city_detail)

        val name = intent.getStringExtra("name")
        val x = intent.getDoubleExtra("x", 0.0)
        val y = intent.getDoubleExtra("y", 0.0)

        val back: Button = findViewById(R.id.exit)

        Log.d("CityDetailActivity", "Name: $name, X: $x, Y: $y")

        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Add marker for the city's coordinates
        val cityLocation = GeoPoint(x, y)
        val cityMarker = Marker(mapView)
        cityMarker.position = cityLocation
        cityMarker.title = "City: $name"
        mapView.overlays.add(cityMarker)

        // Add marker for the current location (use actual coordinates here)
        val currentLocation = GeoPoint(46.55904586721762, 15.638086259355477)
        val currentMarker = Marker(mapView)
        currentMarker.position = currentLocation
        currentMarker.title = "Current Location"
        mapView.overlays.add(currentMarker)

        // Move the map to the city location and zoom in
        val mapController: IMapController = mapView.controller
        mapController.setZoom(18)
        mapController.setCenter(currentLocation)


        back.setOnClickListener()
        {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        //mapView.onPause() // Important for memory management
    }

    override fun onResume() {
        super.onResume()
        //mapView.onResume() // Important for memory management
    }
}
