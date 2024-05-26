package com.example.pgLandlords

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class Profile : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set up the drawer layout and toggle for navigation drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this@Profile, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Set click listeners for profile update and my properties buttons
        val profileUpdateView = findViewById<ImageButton>(R.id.profileUpdateView)
        profileUpdateView.setOnClickListener {
            startActivity(Intent(this, ProfileUpdate::class.java))
        }
        val myPropertiesList = findViewById<ImageButton>(R.id.myPropertysList)
        myPropertiesList.setOnClickListener {
            startActivity(Intent(this, MyProperties::class.java))
        }

        // Set up navigation view and handle menu item clicks
        val navView = findViewById<NavigationView>(R.id.navView)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.myproperties -> {
                    startActivity(Intent(this@Profile, MyProperties::class.java))
                }
                R.id.home -> {
                    startActivity(Intent(this@Profile, Properties::class.java))
                }
                R.id.add_properties_sidemenu -> {
                    startActivity(Intent(this@Profile, CreateProperties::class.java))
                }
                R.id.logout_sidemenu -> {
                    val pgLandlordsPref = this@Profile.getSharedPreferences("pgLandlordsPref", Context.MODE_PRIVATE)
                    val editor = pgLandlordsPref.edit()
                    editor.remove("id")
                    editor.remove("firstName")
                    editor.remove("lastName")
                    editor.remove("userName")
                    editor.apply()
                    val mainIntent = Intent(this@Profile, MainActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mainIntent)
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle toggle button click for navigation drawer
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
