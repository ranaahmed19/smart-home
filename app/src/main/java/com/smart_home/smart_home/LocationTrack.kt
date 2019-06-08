
import android.Manifest
import android.os.Bundle
import android.content.Intent
import android.os.IBinder
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.support.v4.app.ActivityCompat
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.R.string.cancel
import android.content.DialogInterface
import android.support.v4.content.ContextCompat.startActivity
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.location.LocationManager
import android.widget.Toast
import android.content.Context.LOCATION_SERVICE
import android.location.LocationListener



import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.R.string.cancel
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.provider.Settings
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.support.v4.content.ContextCompat.startActivity

class LocationTrack(private val mContext: Context) : Service(), LocationListener {


    internal var checkGPS = false


    internal var checkNetwork = false

    internal var canGetLocation = false

    internal var loc: Location? = null
    internal var latitude: Double = 0.toDouble()
    internal var longitude: Double = 0.toDouble()
    protected var locationManager: LocationManager? = null

    private val location: Location?
        get() {

            try {
                locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager

                checkGPS = locationManager!!
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)
                checkNetwork = locationManager!!
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (!checkGPS && !checkNetwork) {
                    Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show()
                } else {
                    this.canGetLocation = true
                    if (checkGPS) {

                        if (ActivityCompat.checkSelfPermission(
                                mContext,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                mContext,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                        }
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        if (locationManager != null) {
                            loc = locationManager!!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (loc != null) {
                                latitude = loc!!.getLatitude()
                                longitude = loc!!.getLongitude()
                            }
                        }


                    }

                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

            return loc
        }

    init {
        location
    }

    fun getLongitude(): Double {
        if (loc != null) {
            longitude = loc!!.getLongitude()
        }
        return longitude
    }

    fun getLatitude(): Double {
        if (loc != null) {
            latitude = loc!!.getLatitude()
        }
        return latitude
    }

    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)


        alertDialog.setTitle("GPS is not Enabled!")

        alertDialog.setMessage("Do you want to turn on GPS?")


        alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        })


        alertDialog.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })


        alertDialog.show()
    }


    fun stopListener() {
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationManager!!.removeUpdates(this@LocationTrack)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        if (location != null) {
            val lat = location!!.latitude
            val lng = location!!.longitude
            if (lat != 0.0 && lng != 0.0) {
                System.out.println("WE GOT THE LOCATION")
                System.out.println(lat)
                System.out.println(lng)
            }

        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {

    }

    override fun onProviderDisabled(s: String) {

    }

    companion object {


        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10


        private val MIN_TIME_BW_UPDATES = (10000 * 60 * 1).toLong()
    }
}

