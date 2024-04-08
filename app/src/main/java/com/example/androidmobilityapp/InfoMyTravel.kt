package com.example.androidmobilityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InfoMyTravel : AppCompatActivity() {

    private var travel: String = ""
    private var from: String = ""
    private var to: String = ""
    private var date: String = ""
    private var time: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_my_travel)

        travel = intent.getStringExtra(TRAVEL).toString()

        val cities = travel.substringBefore("\n")
        val dateTime = travel.substringAfter("( ")
        from = cities.substringBefore(" -")
        to = cities.substringAfter("- ")
        date = dateTime.substringBefore(" |")
        time = dateTime.substringAfter("| ").dropLast(1)

        val title = "$from - $to"
        //val info = "FROM: $from\n\nTO: $to\n\nDATE: $date\n\nTIME: $time"

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

    /** Called when the user taps the Delete Travel button */
    fun delete(view: View) {

        val db = Firebase.firestore

        db.collection("My Travels")
            .whereEqualTo("from", from)
            .whereEqualTo("to", to)
            .whereEqualTo("date", date)
            .whereEqualTo("time", time)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents){
                    val id = doc.id
                    db.collection("My Travels").document(id)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("DATABASE", "DocumentSnapshot successfully deleted!")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e -> Log.w("DATABASE", "Error deleting document", e) }
                    }
                }
            .addOnFailureListener { exception ->
                Log.w("DATABASE", "Error getting documents: ", exception)
            }
    }
}