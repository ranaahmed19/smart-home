package com.smart_home.smart_home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ToggleButton
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener


class Room1 : AppCompatActivity() {

    var roomsDatabase = FirebaseDatabase.getInstance().getReference("Rooms")
    var light = roomsDatabase.child("Room1").child("Light")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room1)
        val light1TB = findViewById<ToggleButton>(R.id.light1TB)
        light1TB?.setOnCheckedChangeListener { buttonView, isChecked ->
            val msg = "Toggle Button is " + if (isChecked) "ON" else "OFF"
            Toast.makeText(this@Room1, msg, Toast.LENGTH_SHORT).show()
            if(isChecked)
                light.setValue("ON")
            else light.setValue("OFF")
        }



        light.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
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
                // Failed to read value
                Toast.makeText(this@Room1, "failed", Toast.LENGTH_SHORT).show()

            }
        })

    }


}
