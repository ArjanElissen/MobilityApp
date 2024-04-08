package com.example.androidmobilityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InfoTravel : AppCompatActivity() {

    private var destination: Boolean = false
    private var travel: String = ""
    private var from: String = ""
    private var to: String = ""
    private var date: String = ""
    private var time: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_travel)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Android Mobility App"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        travel = intent.getStringExtra(TRAVEL).toString()
        destination = intent.getBooleanExtra(DESTINATION, false)

        val cities = travel.substringBefore("\n")
        val dateTime = travel.substringAfter("( ")
        from = cities.substringBefore(" -")
        to = cities.substringAfter("- ")
        date = dateTime.substringBefore(" |")
        time = dateTime.substringAfter("| ").dropLast(1)

        val title = "$from - $to"
        //val info =  "FROM: $from\n\nTO: $to\n\nDATE: $date\n\nTIME: $time"
        findViewById<TextView>(R.id.titleText).apply {
            text = title
        }

        findViewById<TextView>(R.id.fromText).apply {
            text = from
        }
        findViewById<TextView>(R.id.toText).apply {
            text = to
        }
        findViewById<TextView>(R.id.dateText).apply {
            text = date
        }
        findViewById<TextView>(R.id.timeText).apply {
            text = time
        }
    }

    /** Called when the user taps the Add Travel button */
    fun add(view: View) {

        val travel = Travel(from, to, date, time)

        val db = Firebase.firestore

        db.collection("My Travels")
            .whereEqualTo("from", from)
            .whereEqualTo("to", to)
            .whereEqualTo("date", date)
            .whereEqualTo("time", time)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    db.collection("My Travels")
                        .add(travel)
                        .addOnSuccessListener {
                            Log.d("DATABASE", "DocumentSnapshot successfully written!")
                            // Display the following screen
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }
                }
                else{
                    val text = "You already have this travel"
                    val duration = Toast.LENGTH_SHORT

                    val toast = Toast.makeText(applicationContext, text, duration)
                    toast.show()
                }
            }
            .addOnFailureListener {e -> Log.w("DATABASE", "Error writing document", e)}

        // Display the following screen
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}