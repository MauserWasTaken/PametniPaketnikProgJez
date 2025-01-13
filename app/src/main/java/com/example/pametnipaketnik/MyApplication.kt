package com.example.pametnipaketnik

import android.app.Application

class MyApplication : Application() {
    var scanCounter = 0
    lateinit var OpenedList: MutableList<Opened>

    lateinit var CityList: MutableList<City>

    override fun onCreate() {
        super.onCreate()
        OpenedList = mutableListOf()  // Inicializiramo seznam ob zagonu aplikacije

        CityList = mutableListOf(
            City("John", 10, 20),
            City("Alice", 15, 30),
            City("Bob", 20, 40),
            City("Eve", 25, 50),
            City("Charlie", 30, 60)
        )  // Initialize the list with sample data
    }
}
