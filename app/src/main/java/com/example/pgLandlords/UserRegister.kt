package com.example.pgLandlords

import Database
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class UserRegister : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        // Initialize views
        val button = findViewById<Button>(R.id.signup)
        val firstName = findViewById<EditText>(R.id.editTextFirstName)
        val lastName = findViewById<EditText>(R.id.editTextLastName)
        val userName = findViewById<EditText>(R.id.editTextUsername)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val gender = findViewById<RadioGroup>(R.id.gender)

        // Set click listener for the signup button
        button.setOnClickListener {
            // Validate input fields
            val formValid = validateInput(
                firstName.text.toString(),
                lastName.text.toString(),
                userName.text.toString(),
                password.text.toString(),
                gender
            )
            if (formValid) {
                val selectedGenderGroup = gender.checkedRadioButtonId
                val selectedGender = findViewById<RadioButton>(selectedGenderGroup.toInt())
                // Launch coroutine to add new user
                coroutineScope.launch {
                    val userAdded = addNewUser(
                        firstName.text.toString().trim(),
                        lastName.text.toString().trim(),
                        userName.text.toString().trim(),
                        password.text.toString().trim(),
                        selectedGender.text.toString()
                    )
                    // Show appropriate toast message based on the result
                    if (userAdded == true) {
                        Toast.makeText(
                            this@UserRegister,
                            "User Created successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val mainActivityIntent = Intent(this@UserRegister, MainActivity::class.java)
                        startActivity(mainActivityIntent)
                    } else {
                        Toast.makeText(
                            this@UserRegister,
                            "Unable to create account..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    // Function to add a new user to the database
    private suspend fun addNewUser(
        firstName: String,
        lastName: String,
        userName: String,
        password: String,
        gender: String
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val db = Database(this@UserRegister)
            db.addNewUser(firstName, lastName, userName, password, gender)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Function to validate input fields
    fun validateInput(
        firstName: String,
        lastName: String,
        userName: String,
        password: String,
        gender: RadioGroup
    ): Boolean {
        val selectedGenderGroup = gender.checkedRadioButtonId
        if (firstName.isBlank()) {
            Toast.makeText(this, "First Name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedGenderGroup == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
            return false
        }
        if (userName.isBlank()) {
            Toast.makeText(this, "User Name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isBlank()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
