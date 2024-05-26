package com.example.pgLandlords

import Database
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coroutineScope.launch {
            /*
            first we create a tables after onCreate is called
            and admin is created along with it
             */
            checkAndCreateTables()
        }
        val pgLandlordsPref =
            this.getSharedPreferences("pgLandlordsPref", Context.MODE_PRIVATE)
        // Retrieve user ID from shared preferences
        val userId = pgLandlordsPref.getString("id", null);
        // If user ID exists, start AllProperties.kt activity
        val isAdmin = pgLandlordsPref.getString("isAdmin", "");
        if (userId != null) {
            if (isAdmin == "w"){
                val intentMainActivity = Intent(this, AddLandlord::class.java)
                startActivity(intentMainActivity)
            }
            else {
                val intentMainActivity = Intent(this, Properties::class.java)
                startActivity(intentMainActivity)
            }

        }
        setContentView(R.layout.activity_main)
        val buttonLogin = findViewById<Button>(R.id.login);
        val buttonSignup = findViewById<Button>(R.id.signup);
        buttonSignup.setOnClickListener {
            val intent = Intent(this, UserRegister::class.java)
            startActivity(intent)
        }
        // Button click listener for Login button
        buttonLogin.setOnClickListener {
            val intent = Intent(this, Properties::class.java)
            val addLandLord = Intent(this, AddLandlord::class.java)
            val userName = findViewById<EditText>(R.id.editTextUsernameLogin);
            val password = findViewById<EditText>(R.id.editTextPasswordLogin);
            // Launch a coroutine to perform authentication
            coroutineScope.launch {
                val checkAuthentication =
                    authenticate(userName.text.toString().trim(), password.text.toString().trim())
                if (checkAuthentication[0]) {
                    startActivity(intent)
                    // If authentication is successful, start AllProperties.kt activity and show success toast message
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication successful....",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@MainActivity, "Oops! It seems there was an issue with your login.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    override fun onDestroy() {
        //when activity destroy is called we cancel the coroutine scope.
        super.onDestroy()
        coroutineScope.cancel()
    }

    private suspend fun authenticate(userName: String, password: String): List<Boolean> =
        withContext(Dispatchers.IO) {
            val db = Database(this@MainActivity)
            db.authenticateUser(userName, password)
        }

    private suspend fun checkAndCreateTables(): Boolean =
        withContext(Dispatchers.IO) {
            val db = Database(this@MainActivity)
            db.checkAndCreateTables()
        }
}