package com.example.pametnipaketnik

class Opened(val openedTime: String, val openedPackage: String = "idt", val openedLocation: String = "N") {
    override fun toString(): String {
        return "Opened(time='$openedTime', package='$openedPackage', location='$openedLocation')"
    }
}
