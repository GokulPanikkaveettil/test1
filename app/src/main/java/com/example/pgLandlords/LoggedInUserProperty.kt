package com.example.pgLandlords

import Database
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class MyPropertyViewHolder(itemView: View, listener: MyPropertiesItemAdapter.onItemClickListener) :
    RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.name)
    val description: TextView = itemView.findViewById(R.id.description)
    val price: TextView = itemView.findViewById(R.id.price)
    var propertyId: Int = 0
    val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    val editButton: ImageView = itemView.findViewById(R.id.editButton)

    init {
        // Set click listeners for delete and edit buttons
        deleteButton.setOnClickListener {
            listener.onItemClick(adapterPosition, propertyId, "delete")
        }
        editButton.setOnClickListener {
            listener.onItemClick(adapterPosition, propertyId, "edit")
        }
    }
}

class MyPropertiesItemAdapter(private val properties: List<Property>) :
    RecyclerView.Adapter<MyPropertyViewHolder>() {
    private lateinit var holderListener: onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPropertyViewHolder {
        // Inflate the item layout for each property
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_property_list_layout, parent, false)
        return MyPropertyViewHolder(itemView, holderListener)
    }

    interface onItemClickListener {
        fun onItemClick(position: Int, propertyId: Int, action: String)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        holderListener = listener
    }

    override fun onBindViewHolder(holder: MyPropertyViewHolder, position: Int) {
        // Bind data to the views in each ViewHolder
        val property = properties[position]
        holder.name.text = property.name
        holder.description.text = property.description
        holder.price.text = property.price.toString()
        holder.propertyId = property.id
    }

    override fun getItemCount(): Int = properties.size
}

class MyProperties : AppCompatActivity() {
    var propertyList = mutableListOf<Property>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_propertys)

        coroutineScope.launch {
            // Retrieve user's properties from the database
            val pgLandlordsPref = this@MyProperties.getSharedPreferences("pgLandlordsPref", Context.MODE_PRIVATE)
            val userId = pgLandlordsPref.getString("id", null)
            propertyList = getProperty(userId!!)

            // Set up RecyclerView
            val recyclerView = findViewById<RecyclerView>(R.id.MyProperties)
            recyclerView.layoutManager = LinearLayoutManager(this@MyProperties)

            // If no properties, display a message
            if (propertyList.isEmpty()) {
                Toast.makeText(
                    this@MyProperties,
                    "Currently, there are no properties available in this section.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Configure the adapter for the RecyclerView
            val itemAdapter = MyPropertiesItemAdapter(propertyList)
            recyclerView.adapter = itemAdapter

            // Set item click listener for the RecyclerView
            itemAdapter.setOnItemClickListener(object : MyPropertiesItemAdapter.onItemClickListener {
                override fun onItemClick(position: Int, propertyId: Int, action: String) {
                    // Handle delete and edit actions
                    if (action == "delete") {
                        // Delete property asynchronously
                        coroutineScope.launch {
                            deleteProperty(propertyId)
                            propertyList.removeAt(position)
                            itemAdapter.notifyItemRemoved(position)
                        }
                    } else if (action == "edit") {
                        // Start EditProperty activity to edit property
                        val editPropertyIntent = Intent(this@MyProperties, UpdateProperty::class.java)
                        val property = propertyList[position]
                        editPropertyIntent.putExtra("name", property.name)
                        editPropertyIntent.putExtra("description", property.description)
                        editPropertyIntent.putExtra("propertyId", propertyId)
                        editPropertyIntent.putExtra("price", property.price.toString())
                        startActivity(editPropertyIntent)
                    }
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    // Function to delete property from the database
    private suspend fun deleteProperty(propertyId: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val db = Database(this@MyProperties)
            db.deleteProperty(propertyId)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Function to get user's properties from the database
    private suspend fun getProperty(userId: String): MutableList<Property> = withContext(Dispatchers.IO) {
        return@withContext try {
            val db = Database(this@MyProperties)
            db.getAllProperty(userId, true)
        } catch (e: Exception) {
            e.printStackTrace()
            propertyList
        }
    }
}
