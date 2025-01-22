package com.example.pametnipaketnik

import android.app.Application

class MyApplication : Application() {
    var scanCounter = 0
    lateinit var OpenedList: MutableList<Opened>

    lateinit var packageList: MutableList<Package>

    override fun onCreate() {
        super.onCreate()
        OpenedList = mutableListOf()  // Inicializiramo seznam ob zagonu aplikacije
        packageList = mutableListOf() // Inicializiramo seznam za shranjevanje paketo
    }

}
