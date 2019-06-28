package com.smart_home.smart_home

import android.Manifest
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TrackingService : Service() {

    private var usersDatabase = FirebaseDatabase.getInstance().getReference("Users")
    private var lat : Double = 0.0
    private var long : Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationManager : LocationManager? = null
    private lateinit var locationCallback: LocationCallback


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
    override fun onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.d("loc",location.toString())
                }
            }
        }

        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show()
        super.onCreate()
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        getUserFixedLocation(currentFirebaseUser!!.uid)
        getCurrentLocation()
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show()
    }




    private fun getCurrentLocation(){
        val locationRequest = LocationRequest.create()?.apply {
            interval = 600000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val locationResult = fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,
                null)

        }else {
            Log.e("getCL","permission denied")
        }


    }

    private fun getUserFixedLocation(ID : String) {

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
