package com.smart_home.smart_home

import android.Manifest
import android.app.AlertDialog
import android.app.Service
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.support.annotation.NonNull
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.LatLng

import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Map2Activity : AppCompatActivity() , OnMapReadyCallback {

    private var mLocationPermissionGranted = false
    private var usersDatabase = FirebaseDatabase.getInstance().getReference("Users")
    private var  mLastKnownLocation = null
    private var DEFAULT_ZOOM : Float = 17f
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationManager : LocationManager? = null
    private var latitude : Double = 0.0
    private var longitude : Double= 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map2)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Construct a FusedLocationProviderClient.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val saveLocation = findViewById<TextView>(R.id.saveLocation)
        saveLocation!!.setOnClickListener{
            val currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
            Toast.makeText(this, "" + currentFirebaseUser!!.getUid(), Toast.LENGTH_SHORT).show();
            val currentUserDb = usersDatabase!!.child(currentFirebaseUser!!.getUid())
            currentUserDb.child("Latitude").setValue(latitude)
            currentUserDb.child("Longitude").setValue(longitude)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);

        }

    }



    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(this)


        alertDialog.setTitle("GPS is not Enabled!")

        alertDialog.setMessage("Do you want to turn on GPS?")


        alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            this.startActivity(intent)
        })


        alertDialog.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })


        alertDialog.show()
    }


    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mMap.setMyLocationEnabled(true)
                mMap.getUiSettings().setMyLocationButtonEnabled(true)
                getDeviceLocation()

            } else {
                mMap.setMyLocationEnabled(false)
                mMap.getUiSettings().setMyLocationButtonEnabled(false)
                mLastKnownLocation = null
                showSettingsAlert()
                Log.i("Exception: %s","get perm")

            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }


    private fun getDeviceLocation() {
        /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val locationResult = fusedLocationClient.lastLocation

                locationResult.addOnSuccessListener { location : Location? ->
                    val Here = LatLng(location!!.latitude,
                        location!!.longitude)
                    mMap.addMarker(MarkerOptions().position(Here).title("Marker in Here"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Here))
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            Here, DEFAULT_ZOOM
                        ))
                    Toast.makeText(
                        applicationContext,
                        "Longitude:" + java.lang.Double.toString(location!!.longitude) + "\nLatitude:" + java.lang.Double.toString(
                            location!!.latitude
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                    latitude = location!!.latitude
                    longitude = location!!.longitude



                    // Got last known location. In some rare situations this can be null.
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map

        // Do other setup activities here too, as described elsewhere in this tutorial.

        // Turn on the My Location layer and the related control on the map.
       updateLocationUI()
       // getDeviceLocation()
        // Get the current location of the device and set the position of the map.

    }
}
