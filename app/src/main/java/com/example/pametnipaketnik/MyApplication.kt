package com.example.pametnipaketnik

import android.app.Application

class MyApplication : Application() {
    var scanCounter = 0
    lateinit var OpenedList: MutableList<Opened>

    lateinit var packageList: MutableList<Package>

    override fun onCreate() {
        super.onCreate()
        OpenedList = mutableListOf()  // Inicializiramo seznam ob zagonu aplikacije

        packageList = mutableListOf(
            Package("John", 10, 20),
            Package("Alice", 15, 30),
            Package("Bob", 20, 40),
            Package("Eve", 25, 50),
            Package("Charlie", 30, 60)
        )  // Initialize the list with sample data
    }
}
