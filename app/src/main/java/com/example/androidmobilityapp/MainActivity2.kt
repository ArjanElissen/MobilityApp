package com.example.androidmobilityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    /** Called when the user taps the Yes button */
    fun car(view: View) {
        val intent = Intent(this, NavigationCar::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the No button */
    fun noCar(view: View) {
        val intent = Intent(this, NavigationNoCar::class.java)
        startActivity(intent)
    }

}