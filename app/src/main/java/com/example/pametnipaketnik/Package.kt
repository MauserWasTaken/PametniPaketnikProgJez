package com.example.pametnipaketnik

class Package (val name: String, val x: Double, val y: Double) {
    override fun toString(): String {
        return "Opened(name='$name', x='$x', y='$y')"
    }
}