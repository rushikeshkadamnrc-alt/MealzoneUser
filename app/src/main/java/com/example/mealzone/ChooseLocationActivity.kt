package com.example.mealzone

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity

class ChooseLocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_location)
        val listOfLocation = findViewById<AutoCompleteTextView>(R.id.listOfLocation)

        // 2) Locations ki list banao (example)
        val locations = arrayOf("Mumbai", "Pune", "Delhi", "Bangalore", "Hyderabad")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locations)
        listOfLocation.setAdapter(adapter)

        listOfLocation.setOnItemClickListener { _, _, position, _ ->
            val selectedLocation = locations[position]

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
