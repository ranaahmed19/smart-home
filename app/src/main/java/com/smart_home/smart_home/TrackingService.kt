package com.smart_home.smart_home

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TrackingService : Service() {

    private var usersDatabase = FirebaseDatabase.getInstance().getReference("Users")
    private var lat : Double = 0.0
    private var long : Double = 0.0


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
    override fun onCreate() {

        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show()
        super.onCreate()
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        getLocation(currentFirebaseUser!!.uid)
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show()
    }

    private fun getLocation(ID : String) {

        val user = usersDatabase.child(ID)
        val userLat = user.child("Latitude")
        val userLong = user.child("Longitude")
        userLat.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                lat = dataSnapshot.getValue() as (Double)
                Log.d("latlong",lat.toString())
            }
            override fun onCancelled(error: DatabaseError) {
               Log.e("ID","error")
            }
        })
        userLong.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                long = dataSnapshot.getValue() as (Double)
                Log.d("latlong",long.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ID2","error")
            }
        })


    }
}
