package com.example.pametnipaketnik

class Package (val name: String, val x: Int, val y: Int) {
    override fun toString(): String {
        return "Opened(name='$name', x='$x', y='$y')"
    }
}