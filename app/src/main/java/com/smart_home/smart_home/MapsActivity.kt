package com.smart_home.smart_home

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import java.util.ArrayList;
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private var usersDatabase = FirebaseDatabase.getInstance().getReference("Users")
    private var DEFAULT_ZOOM : Float = 17f
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationManager : LocationManager? = null
    private var latitude : Double = 0.0
    private var longitude : Double= 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // Construct a FusedLocationProviderClient.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val saveLocation = findViewById<TextView>(R.id.saveLocation)
        saveLocation!!.setOnClickListener{

            if(latitude != 0.0 && longitude != 0.0){
                //getDeviceLocation()
                val currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                val currentUserDb = usersDatabase!!.child(currentFirebaseUser!!.getUid())
                currentUserDb.child("Latitude").setValue(latitude)
                currentUserDb.child("Longitude").setValue(longitude)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent);
            }
            else{
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                    updateLocationUI()
                }else Toast.makeText(
                    applicationContext, "Permission denied or GPS is turned off",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

    }

    fun showSettingsAlert() {
        val alertDialog = android.app.AlertDialog.Builder(this)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocationUI()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Location permission is needed"
                        ,
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }


    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);
            }else{
                if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMapToolbarEnabled = true
                    getDeviceLocation()

                } else {
                    showSettingsAlert()
                    Log.i("Exception: %s","get perm")

                }
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
            Log.d("outside","get perm")
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val locationResult = fusedLocationClient.lastLocation
                Log.d("inside","locationRes0")
                locationResult.addOnSuccessListener { location : Location? ->
                    if(location == null){
                        Toast.makeText(
                            applicationContext,
                            "Location isn't available right now. Try again in a few seconds",
                            Toast.LENGTH_LONG
                        ).show()
                    }else {
                    val Here = LatLng(location!!.latitude,
                        location!!.longitude)
                    Log.d("inside2323",Here.toString())
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
                }}
                locationResult.addOnFailureListener {
                    Log.e("Exception: %s", "eeeee")
                }          }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        updateLocationUI()
    }
}
