package com.example.androidmobilityapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class NavigationCar : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private lateinit var city: TextView
    private val locationPermissionCode: Int = 2
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var previewSelectedTimeTextView: TextView

    private var date: String = ""
    private var time: String = ""

    // listener which is triggered when the
    // time is picked from the time picker dialog
    private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hour, minute -> // logic to properly handle
            // the picked timings by user
            val formattedTime: String = when {
                minute < 10 -> {
                    "${hour}:0${minute}"

                }
                else -> {
                    "${hour}:${minute}"
                }

            }

            previewSelectedTimeTextView.text = formattedTime
        }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SetTextI18n", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_car)

        val cities = arrayOf("Amsterdam","Budel", "Culemborg","Den Haag","Eindhoven", "Franeker","Groningen","Haarlem","IJmuiden","Joure","Katwijk", "Leerdam", "Maastricht", "Naarden","Oostdijk", "Purmerend", "Roermond", "Slochteren", "Twello","Arnhem","Utrecht","Valkenburg", "Waalwijk","Ysselstyn",  "Zwolle", "Alkmaar")

        //Creating the instance of ArrayAdapter containing list of fruit names
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.select_dialog_item, cities)
        //Getting the instance of AutoCompleteTextView
        val actv = findViewById<View>(R.id.fromText) as AutoCompleteTextView
        actv.threshold = 1 //will start working from first character
        actv.setAdapter(adapter) //setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.BLACK)

        //Getting the instance of AutoCompleteTextView
        val actv2 = findViewById<View>(R.id.toText) as AutoCompleteTextView
        actv2.threshold = 1 //will start working from first character
        actv2.setAdapter(adapter) //setting the adapter data into the AutoCompleteTextView
        actv2.setTextColor(Color.BLACK)

       // val createBill: EditText = findViewById(R.id.editTextNumberDecimal)
        // createBill.setText(getString( R.string.AmountBill))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        val button: Button = findViewById(R.id.getLocation)
        button.setOnClickListener {
            startLocationListening()
        }

        // Get the Intent that started this activity and extract the string
        val from = intent.getStringExtra(FROM)
        val to = intent.getStringExtra(TO)

        val current = LocalDateTime.now()
        val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")
        val formatted = current.format(pattern)

        date = formatted.subSequence(0, 10).toString()
        time = formatted.subSequence(11, 16).toString()

        //Calendar
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val calendarText = findViewById<EditText>(R.id.dateText)

        val h = Integer.parseInt(time.substringBefore(':'))
        val m = Integer.parseInt(time.substringAfter(':'))

        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.fromText).apply {
            text = from
        }
        findViewById<TextView>(R.id.toText).apply {
            text = to
        }
        findViewById<TextView>(R.id.dateText).apply {
            val actualMonth = month + 1
            text = "$day/$actualMonth/$year"
        }
        findViewById<TextView>(R.id.timeText).apply {
            text = time
        }

        //Onclick show date
        calendarText.setOnClickListener {
            val dpd = DatePickerDialog( this,
                { _, mYear, mMonth, mDay -> calendarText.setText(mDay.toString() + "/"+ (mMonth+1).toString() +"/"+ mYear.toString())}, year, month, day)
            dpd.show()
        }
        // instance of the UI elements
        val buttonPickTime = findViewById<TextView>(R.id.timeText)
        previewSelectedTimeTextView = findViewById(R.id.timeText)

        // handle the pick time button to
        // open the TimePickerDialog
        buttonPickTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                // pass the Context
                this,
                // listener to perform task
                // when time is picked
                timePickerDialogListener,
                // default hour when the time picker
                // dialog is opened
                h,
                // default minute when the time picker
                // dialog is opened
                m,
                // 24 hours time picker is
                // false (varies according to the region)
                false
            )

            // then after building the timepicker
            // dialog show the dialog to user
            timePicker.show()
        }
    }

    private fun getCityName(lat: Double,long: Double):String{
        var cityName = ""
        var countryName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat,long,3)
        val cityText = findViewById<EditText>(R.id.fromText)

        if (address != null) {
            cityName = address[0].locality
        }
        if (address != null) {
            countryName = address[0].countryName
        }
        cityText.setText((cityName))  //" ; your Country " + countryName
        return cityName
    }

    private fun startLocationListening() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        //val info = getString(R.string.Latitude)
        //tvGpsLocation = findViewById(R.id.newTextView)
        //tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
        getCityName(location.latitude, location.longitude)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Called when the user taps the Find button */
    fun create(view: View) {
        val fromText = findViewById<EditText>(R.id.fromText)
        val from = fromText.text.toString()

        val toText = findViewById<EditText>(R.id.toText)
        val to = toText.text.toString()

        val dateText = findViewById<EditText>(R.id.dateText)
        val date = dateText.text.toString()

        val timeText = findViewById<EditText>(R.id.timeText)
        val time = timeText.text.toString()



        val db = Firebase.firestore

        // Create a new travel
        val travel = Travel(from, to, date, time)

        if (from == "" || to == "") {
            val text = "Fill the location and destination"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        }
        else if (!futureTravel(date, time)) {
            val text = "The travel should be in the future"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        }
        else{
            // Add a new document with a generated ID if you don't have it
            db.collection("My Travels")
                .whereEqualTo("from", from)
                .whereEqualTo("to", to)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        db.collection("Travels")
                            .add(travel)
                            .addOnSuccessListener { Log.d("DATABASE", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }

                        db.collection("My Travels")
                            .add(travel)
                            .addOnSuccessListener { Log.d("DATABASE", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }
                        // Display the following screen
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        val text = "You already have this travel"
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(applicationContext, text, duration)
                        toast.show()
                    }
                }
                .addOnFailureListener { e -> Log.w("DATABASE", "Error writing document", e) }
        }
    }

    fun popTimePicker(view: View) {}

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