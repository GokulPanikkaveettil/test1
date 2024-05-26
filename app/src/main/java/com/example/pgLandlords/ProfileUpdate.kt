package com.example.pgLandlords

import Database
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.*

class ProfileUpdate : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_update)

        // Initialize views and retrieve user data from shared preferences
        val updateFirstName = findViewById<EditText>(R.id.editTextUpdateFirstName)
        val updateLastName = findViewById<EditText>(R.id.editTextUpdateLastName)
        val pgLandlordsPref = this.getSharedPreferences("pgLandlordsPref", Context.MODE_PRIVATE)
        val userId = pgLandlordsPref.getString("id", null)

        // Redirect to MainActivity if user ID is null
        if (userId == null) {
            val intentMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentMainActivity)
        }

        // Set initial values for first name and last name EditText fields
        val firstName = pgLandlordsPref.getString("firstName", null)
        val lastName = pgLandlordsPref.getString("lastName", null)
        updateFirstName.setText(firstName)
        updateLastName.setText(lastName)

        // Update profile button click listener
        val updateButton = findViewById<Button>(R.id.updateProfileButton)
        updateButton.setOnClickListener {
            // Check if first name and last name are not blank
            if (updateFirstName.text.isBlank() or updateLastName.text.isBlank()) {
                Toast.makeText(this@ProfileUpdate, "First name and last name cannot be blank", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Launch coroutine to update profile
                coroutineScope.launch {
                    val updated = updateProfile(updateFirstName.text.toString(), updateLastName.text.toString())
                    if (updated) {
                        // Navigate to Properties activity after successful update
                        val intent = Intent(this@ProfileUpdate, Properties::class.java)
                        startActivity(intent)
                    } else {
                        // Show toast message if update fails
                        Toast.makeText(this@ProfileUpdate, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel coroutine scope on activity destroy
        coroutineScope.cancel()
    }

    private suspend fun updateProfile(firstName: String, lastName: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            // Update profile in the database
            val db = Database(this@ProfileUpdate)
            db.updateProfile(firstName, lastName)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
