package com.smart_home.smart_home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener


class Room1 : AppCompatActivity() {

    private var roomsDatabase = FirebaseDatabase.getInstance().getReference("Rooms")
    private var light = roomsDatabase.child("Room1").child("Light")
    private var curtains = roomsDatabase.child("Room1").child("Curtains")
    private var temperature = roomsDatabase.child("Room1").child("Temperature")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room1)
        controlLight()
        controlCurtains()
        controlTemperature()
    }

    private fun controlTemperature(){
        val temp1ET = findViewById<TextView>(R.id.readTemperature1TV)
        temperature.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Long::class.java)
                temp1ET.setText(value!!.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Room1, "failed", Toast.LENGTH_SHORT).show()
            }
        })

    }


    private fun controlCurtains(){
        val curtains1TB = findViewById<ToggleButton>(R.id.curtains1TB)
        curtains1TB?.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                curtains.setValue("Opened")
            else curtains.setValue("Closed")
        }
        curtains.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                if (value == "Opened") {
                    curtains1TB.setTextOn("Opened")
                    curtains1TB.setChecked(true)
                } else {
                    curtains1TB.setTextOff("Closed")
                    curtains1TB.setChecked(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Room1, "failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun controlLight(){
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
                Toast.makeText(this@Room1, "failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
