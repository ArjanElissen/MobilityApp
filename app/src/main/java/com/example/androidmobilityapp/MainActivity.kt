package com.example.androidmobilityapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


const val CAR = "com.example.androidmobilityapp.CAR"
const val TRAVEL = "com.example.androidmobilityapp.TRAVEL"
const val DESTINATION = "com.example.androidmobilityapp.DESTINATION"

class MainActivity : AppCompatActivity() {

    private val locationPermissionCode: Int = 2
    private var date: String = ""
    private var time: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val current = LocalDateTime.now()
        val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")
        val formatted = current.format(pattern)

        date = formatted.subSequence(0, 10).toString()
        time = formatted.subSequence(11, 16).toString()

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !==
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }

        val db = Firebase.firestore

        db.collection("My Travels")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents){
                    val s = doc.data
                    if (!futureTravel(s["date"].toString(), s["time"].toString())) {
                        val id = doc.id
                        //Log.d("DATABASE", s["from"].toString() + " - " +  s["to"].toString())
                        db.collection("My Travels").document(id)
                            .delete()
                            .addOnSuccessListener { Log.d("DATABASE", "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w("DATABASE", "Error deleting document", e) }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("DATABASE", "Error getting documents: ", exception)
            }

        var array = arrayOf<String>()

        db.collection("My Travels")
            .get()
            .addOnSuccessListener { documents ->
                //array = arrayOf<String>()
                for (doc in documents) {
                    val s = doc.data
                    array += s["from"].toString() + " - " + s["to"]
                        .toString() + "\n \t( " + s["date"].toString() + " | " + s["time"]
                        .toString() + ")"
                }
                if (array.isEmpty()){
                    array += "No travels yet"
                    val listView = findViewById<ListView>(R.id.list)
                    val adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, array)
                    listView.adapter = adapter
                }
                else{
                    val listView = findViewById<ListView>(R.id.list)
                    val adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, array)
                    listView.adapter = adapter

                    listView.setOnItemClickListener { _, _, position, _ ->

                        // Display the following screen
                        val intent = Intent(this, InfoMyTravel::class.java).apply {
                            putExtra(TRAVEL, array[position])
                            putExtra(DESTINATION, true)
                        }
                        startActivity(intent)

                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("DATABASE", "Error getting documents: ", exception)
            }

    }

    /** Called when the user taps the New trip button */
    fun newTrip(view: View) {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun futureTravel(date: String, time: String): Boolean {
        val travelDay = Integer.parseInt(date.substringBefore('/'))
        val travelMonth = Integer.parseInt(date.substringAfter('/').substringBefore('/'))
        val travelYear = Integer.parseInt(date.substringAfterLast('/'))

        val actualDay = Integer.parseInt(this.date.substringBefore('/'))
        val actualMonth = Integer.parseInt(this.date.substringAfter('/').substringBefore('/'))
        val actualYear = Integer.parseInt(this.date.substringAfterLast('/'))

        val travelHour = Integer.parseInt(time.substringBefore(':'))
        val travelMinute = Integer.parseInt(time.substringAfter(':'))
        val travelTime = travelHour*60 + travelMinute

        val actualHours = Integer.parseInt(this.time.substringBefore(':'))
        val actualMinutes = Integer.parseInt(this.time.substringAfter(':'))
        val actualTime = actualHours*60 + actualMinutes

        if (actualYear<travelYear) return true
        if (actualYear==travelYear){
            if (actualMonth<travelMonth) return true
            if (actualMonth==travelMonth){
                if (actualDay<travelDay) return true
                if (actualDay==travelDay){
                    if (actualTime<=travelTime) return true
                }
            }
        }
        return false
    }
}