package com.smart_home.smart_home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import android.widget.ToggleButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Bathroom : AppCompatActivity() {
    private var roomsDatabase = FirebaseDatabase.getInstance().getReference("Rooms")
    private var light = roomsDatabase.child("Room3").child("Light")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bathroom)
        if (supportActionBar != null)
            supportActionBar?.hide()
        controlLight()
    }
    private fun controlLight(){
        val light1TB = findViewById<ToggleButton>(R.id.light1TB)
        light1TB?.setOnCheckedChangeListener { buttonView, isChecked ->
            val msg = "Toggle Button is " + if (isChecked) "ON" else "OFF"
            Toast.makeText(this@Bathroom, msg, Toast.LENGTH_SHORT).show()
            if(isChecked)
                light.setValue("ON")
            else light.setValue("OFF")
        }

        light.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                if (value == "ON") {
                    light1TB.setTextOn("ON")
                    light1TB.setChecked(true)
                } else {
                    light1TB.setTextOff("OFF")
                    light1TB.setChecked(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Bathroom, "failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}