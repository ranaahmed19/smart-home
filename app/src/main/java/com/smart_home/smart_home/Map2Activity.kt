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
import android.location.LocationManager
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.annotation.NonNull
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.LatLng

import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener



class Map2Activity : AppCompatActivity(), OnMapReadyCallback {

    private var mLocationPermissionGranted = false
    private var  mLastKnownLocation = null
    private var DEFAULT_ZOOM : Float = 17f
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map2)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Construct a FusedLocationProviderClient.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun getLocationPermission() {
        /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            //showSettingsAlert()

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }
    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true)
                mMap.getUiSettings().setMyLocationButtonEnabled(true)
            } else {
                mMap.setMyLocationEnabled(false)
                mMap.getUiSettings().setMyLocationButtonEnabled(false)
                mLastKnownLocation = null
                getLocationPermission()
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
            if (mLocationPermissionGranted) {
                val locationResult = fusedLocationClient.lastLocation
               /* locationResult.addOnCompleteListener(this, object : OnCompleteListener {
                    override fun onComplete(task: Task<Location>) {
                        if (task.isSuccessful) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.result
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        mLastKnownLocation.get,
                                        mLastKnownLocation.getLongitude()
                                    ), DEFAULT_ZOOM
                                )
                            )
                        } else {
                            Log.d("henaa", "Current location is null. Using defaults.")
                            Log.e("henaa", "Exception: %s", task.exception)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))
                            mMap.getUiSettings().setMyLocationButtonEnabled(false)
                        }
                    }
                })
                */
                locationResult.addOnSuccessListener { location : Location? ->
                    val Here = LatLng(location!!.latitude,
                        location!!.longitude)
                    mMap.addMarker(MarkerOptions().position(Here).title("Marker in Here"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Here))
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            Here, DEFAULT_ZOOM
                        ))

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

        // Get the current location of the device and set the position of the map.
       // getDeviceLocation()
    }
}
