package com.example.pgLandlords

import Database
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.*

class GetLandLordDetails : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main) // Create a coroutine scope
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_reply) // Set the layout for the activity
        val propertyId = intent.getStringExtra("propertyId") // Get the property ID from the intent
        val phone = findViewById<TextView>(R.id.phone) // Find the TextView for displaying phone number

        coroutineScope.launch {
            // Launch a coroutine to fetch landlord details
            val fetchedphone = getLandLordDetails(propertyId!!.toInt())
            phone.setText("Contact Landlord :" + fetchedphone) // Set the phone number in the TextView
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancel all coroutines when the activity is destroyed
    }

    // Function to fetch landlord details from the database
    private suspend fun getLandLordDetails(propertyId: Int): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val db = Database(this@GetLandLordDetails)
            db.getLandLordDetails(propertyId)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
