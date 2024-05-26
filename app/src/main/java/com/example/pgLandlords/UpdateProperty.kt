package com.example.pgLandlords

import Database
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class UpdateProperty : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_or_update_properties)

        // Retrieve property details from intent extras
        val propertyId = intent.getIntExtra("propertyId", 0)
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val price = intent.getStringExtra("price")

        // Initialize EditText fields with property details
        val editName = findViewById<EditText>(R.id.name)
        val editDescription = findViewById<EditText>(R.id.description)
        val editPrice = findViewById<EditText>(R.id.price)
        editName.setText(name)
        editDescription.setText(description)
        editPrice.setText(price)

        // Button to update property details
        val updateButton = findViewById<Button>(R.id.addProperty)
        updateButton.setOnClickListener {
            // Check if name field is not blank
            if (editName.text.isBlank()) {
                Toast.makeText(
                    this,
                    "Name cannot be empty.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Launch coroutine to update property
                coroutineScope.launch {
                    val updated = updateProperty(
                        propertyId!!.toInt(),
                        editName.text.toString(),
                        editDescription.text.toString(),
                        editPrice.text.toString()
                    )
                    if (updated) {
                        // Redirect to MyProperties activity after successful update
                        startActivity(Intent(this@UpdateProperty, MyProperties::class.java))
                    } else {
                        // Show toast message if update fails
                        Toast.makeText(
                            this@UpdateProperty,
                            "Failed to update property. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel coroutine scope when activity is destroyed
        coroutineScope.cancel()
    }

    private suspend fun updateProperty(propertyId: Int, name: String, description: String, price: String): Boolean = withContext(
        Dispatchers.IO
    ) {
        return@withContext try {
            // Update property in the database
            val db = Database(this@UpdateProperty)
            db.editProperty(propertyId, name, description, price.toInt())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
