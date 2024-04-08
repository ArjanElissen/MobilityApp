package com.example.androidmobilityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class TravelPossibilities : AppCompatActivity() {

    private var car: Boolean = false
    private var from: String = ""
    private var to: String = ""
    private var date: String = ""
    private var time: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_possibilities)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Android Mobility App"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        // Get the Intent that started this activity and extract the fields
        car = intent.getBooleanExtra(CAR, false)
        from = intent.getStringExtra(FROM).toString()
        to = intent.getStringExtra(TO).toString()
        date = intent.getStringExtra(DATE).toString()
        time = intent.getStringExtra(TIME).toString()

        val db = Firebase.firestore

        db.collection("Travels")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents){
                    val s = doc.data
                    if (!futureTravel(s["date"].toString(), s["time"].toString())) {
                        val id = doc.id
                        //Log.d("DATABASE", s["from"].toString() + " - " +  s["to"].toString())
                        db.collection("Travels").document(id)
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

        if (to == "" && from == ""){
            db.collection("Travels")
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents){
                        val s = doc.data
                        array += s["from"].toString() + " - " + s["to"].toString()  + "\n \t( " + s["date"].toString() + " | " + s["time"].toString() + ")"
                    }
                    if (array.isEmpty()){
                        array += "No travels matching your characteristics"
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
                            val intent = Intent(this, InfoTravel::class.java).apply {
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
        else if (to == ""){
            db.collection("Travels")
                .whereEqualTo("from", from)
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents){
                        val s = doc.data
                        array += s["from"].toString() + " - " + s["to"].toString()  + "\n \t( " + s["date"].toString() + " | " + s["time"].toString() + ")"
                    }
                    if (array.isEmpty()){
                        array += "No travels matching your characteristics"
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
                            val intent = Intent(this, InfoTravel::class.java).apply {
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
        else if (from == ""){
            db.collection("Travels")
                .whereEqualTo("to", to)
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents){
                        val s = doc.data
                        array += s["from"].toString() + " - " + s["to"].toString()  + "\n \t( " + s["date"].toString() + " | " + s["time"].toString() + ")"
                    }
                    if (array.isEmpty()){
                        array += "No travels matching your characteristics"
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
                            val intent = Intent(this, InfoTravel::class.java).apply {
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
        else {
            db.collection("Travels")
                .whereEqualTo("from", from)
                .whereEqualTo("to", to)
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents){
                        val s = doc.data
                        array += s["from"].toString() + " - " + s["to"].toString()  + "\n \t( " + s["date"].toString() + " | " + s["time"].toString() + ")"
                    }
                    if (array.isEmpty()){
                        array += "No travels matching your characteristics"
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
                            val intent = Intent(this, InfoTravel::class.java).apply {
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

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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