package com.smart_home.smart_home


import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.IBinder
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
import com.google.maps.android.SphericalUtil
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback






class TrackingService : Service() {

    private var usersDatabase = FirebaseDatabase.getInstance().getReference("Users")
    private var roomsDatabase = FirebaseDatabase.getInstance().getReference("Rooms")
    private var lightRoom1 = roomsDatabase.child("Room1").child("Light")
    private var lightRoom2 = roomsDatabase.child("Room2").child("Light")
    private var lightRoom3 = roomsDatabase.child("Room3").child("Light")
    private var lat : Double = 0.0
    private var long : Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationManager : LocationManager? = null
    private lateinit var locationCallback: LocationCallback
    private val minDistance : Double = 100.0
   // private val timeInterval :Long  = 6000

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
                    if(lat !=0.0 && long != 0.0 ){
                        Log.d("loc",location.toString())
                        val currentLatlng = LatLng(location.latitude,location.longitude)
                        val fixedLatlng = LatLng(lat,long)
                        var currentDistance = distanceBetween(fixedLatlng,currentLatlng)
                        Log.d("distance1",currentDistance.toString())
                        if(currentDistance!! > minDistance){
                            lightRoom1.setValue("OFF")
                            lightRoom2.setValue("OFF")
                            lightRoom3.setValue("OFF")
                        }else {
                            lightRoom1.setValue("ON")
                            lightRoom2.setValue("ON")
                            lightRoom3.setValue("ON")
                        }
                    }
                }
            }
        }
        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show()
        super.onCreate()
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser ;
        getUserFixedLocation(currentFirebaseUser!!.uid)
        getCurrentLocation()
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show()
        isServiceRunning = true
        return START_STICKY
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
        isServiceRunning = false
        Log.d("tracking","service destroyed")
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show()
    }

    fun distanceBetween(point1: LatLng?, point2: LatLng?): Double? {
        return if (point1 == null || point2 == null) {
            null
        } else SphericalUtil.computeDistanceBetween(point1, point2)

    }

    private fun getCurrentLocation(){
        val locationRequest = LocationRequest.create()?.apply {
            interval = 6000
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
                lat = dataSnapshot.value as (Double)
                Log.d("latlong",lat.toString())
            }
            override fun onCancelled(error: DatabaseError) {
               Log.e("ID","error")
            }
        })
        userLong.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                long = dataSnapshot.value as (Double)
                Log.d("latlong",long.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ID2","error")
            }
        })


    }
    companion object {
        var isServiceRunning = false
    }
}
