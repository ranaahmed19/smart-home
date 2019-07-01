package com.smart_home.smart_home

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.CompoundButton
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener
import com.mikepenz.materialdrawer.model.*
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.DialogInterface
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.annotations.NotNull
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var usersDatabase = FirebaseDatabase.getInstance().getReference("Users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DrawerBuilder().withActivity(this).build()
        var toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        buildNav(toolbar,this)
        if (supportActionBar != null)
            supportActionBar?.hide()
        var room1Button = findViewById<FrameLayout>(R.id.room1BT)
        var room2Button = findViewById<FrameLayout>(R.id.room2BT)
        var kitchenButton = findViewById<FrameLayout>(R.id.kitchenLayout)
        var bathroomButton = findViewById<FrameLayout>(R.id.bathroomLayout)

        room1Button.setOnClickListener {
            val intent = Intent(this, Room1Activity::class.java)
            startActivity(intent)
        }

        kitchenButton.setOnClickListener {
            val intent = Intent(this, Kitchen::class.java)
            startActivity(intent)
        }

        room2Button.setOnClickListener {
            val intent = Intent(this,Room2Activity ::class.java)
            startActivity(intent)
        }
        bathroomButton.setOnClickListener {
            val intent = Intent(this, Bathroom::class.java)
            startActivity(intent)
        }
        startService()

    }

    private fun buildNav(toolbar:Toolbar,activity: MainActivity) {
        auth = FirebaseAuth.getInstance()
        val user = auth!!.currentUser
        val mail = user!!.email
        val item2 = PrimaryDrawerItem().withIdentifier(1).withName("Sign Out").withSetSelected(false)
        val awayMode = SwitchDrawerItem().withIdentifier(2).withSwitchEnabled(true)
            .withName("Away Mode").withSetSelected(false).withOnCheckedChangeListener(object : OnCheckedChangeListener {
                override fun onCheckedChanged(
                    drawerItem: IDrawerItem<*, *>?,
                    buttonView: CompoundButton?,
                    isChecked: Boolean
                ) {if(isChecked){
                    controlToggleButton(isChecked, user.uid, buttonView)
                }else {
                    val intent = Intent(activity,TrackingService::class.java)
                    stopService(intent)
                }} })
            .withChecked(TrackingService.isServiceRunning)
        val map = PrimaryDrawerItem().withIdentifier(3).withName("Update Home Location").withSetSelected(false)

        val headerResult = AccountHeaderBuilder().withActivity(this)
            .addProfiles(
                ProfileDrawerItem().withIcon(getResources().getDrawable(R.drawable.logo)).withTextColor(Color.BLACK).withEmail(mail).withName("My Home")
            )
            .build()

        val result = DrawerBuilder()
            .withActionBarDrawerToggleAnimated (true)
            .withTranslucentStatusBar(true)
            .withDisplayBelowStatusBar(true)
            .withAccountHeader(headerResult)
            .withActivity(this)
            .withToolbar(toolbar)
            .addDrawerItems(
                item2,
                awayMode,
                map
            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*,*>): Boolean {
                    if(position == 1) {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    else if (position == 2){

                    }
                    else if(position == 3){
                        if(TrackingService.isServiceRunning){
                            Toast.makeText(activity, "Turn off Away Mode first", Toast.LENGTH_LONG).show()
                        }else {
                        val intent = Intent(activity, MapsActivity::class.java)
                        startActivity(intent)
                    }}

                    return false
                }
            })
            .build()

    }

    private fun startService() {
        var serviceIntent  = Intent(this, NotificationService::class.java)
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")

        ContextCompat.startForegroundService(this, serviceIntent)
    }
//,myCallback: (result: String?) -> Boolean
    private fun checkUserLocationAvailability(ID: String ) :Boolean{
        val user = usersDatabase.child(ID)
        val userLat = user.child("Latitude")
        var locationFound = true
        var v=userLat.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value == null) {
                    Log.d("snapshot","fadya")
                  //  locationFound = myCallback.invoke("false")

                }
                //else locationFound = myCallback.invoke("true")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ID","error")
            }
        })
        return locationFound
    }
    private fun controlToggleButton(isChecked: Boolean, ID: String, buttonView: CompoundButton?){
        if(isChecked){
            if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ){
                if(checkUserLocationAvailability(ID)){
                    val intent = Intent(this,TrackingService::class.java)
                    startService(intent)
                }else {Toast.makeText(this, "Update home location first", Toast.LENGTH_LONG).show()
                    buttonView?.isChecked = false
                }

            }else ActivityCompat.requestPermissions(this@MainActivity,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        }else {
            val intent = Intent(this,TrackingService::class.java)
            stopService(intent)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this,TrackingService::class.java)
                    startService(intent)
                }
                return
            }
        }
    }
    private fun showSettingsAlert() {
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

}