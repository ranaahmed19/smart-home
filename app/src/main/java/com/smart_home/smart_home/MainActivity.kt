package com.smart_home.smart_home

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Button
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DrawerBuilder().withActivity(this).build()
        var toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        buildNav(toolbar,this)
        if (supportActionBar != null)
            supportActionBar?.hide()
        var room1Button = findViewById<Button>(R.id.room1BT) as FrameLayout
        var room2Button = findViewById<Button>(R.id.room2BT) as FrameLayout
        var room3Button = findViewById<Button>(R.id.kitchenLayout) as FrameLayout
        val user = FirebaseAuth.getInstance().currentUser
        Toast.makeText(this@MainActivity, "You clicked me."+user!!.email, Toast.LENGTH_LONG).show()
        room1Button.setOnClickListener {
            // your code to perform when the user clicks on the button
            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Room1::class.java)
            startActivity(intent);
        }
        room2Button.setOnClickListener { // used as a log out button until we make a log out button
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent);
        }
        startService()

        room3Button.setOnClickListener {
            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Map2Activity::class.java)
            startActivity(intent);
        }
    }

    private fun buildNav(toolbar:Toolbar,activity: MainActivity) {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        auth = FirebaseAuth.getInstance()
        val user = auth!!.currentUser
        val mail = user!!.email
        val item2 = PrimaryDrawerItem().withIdentifier(1).withName("Sign Out").withSetSelected(false)
        val item3 = PrimaryDrawerItem().withIdentifier(2).withName("Away Mode").withSetSelected(false)
        val item4 = PrimaryDrawerItem().withIdentifier(3).withName(" turn off Away Mode").withSetSelected(false)
        val headerResult = AccountHeaderBuilder().withActivity(this)
            .addProfiles(
                ProfileDrawerItem().withIcon(getResources().getDrawable(R.drawable.logo)).withTextColor(Color.BLACK).withEmail(mail).withName("My Home")
            )
            .build()

//create the drawer and remember the `Drawer` result object
        val result = DrawerBuilder()
            .withActionBarDrawerToggleAnimated (true)
            .withTranslucentStatusBar(true)
            .withDisplayBelowStatusBar(true)
            .withAccountHeader(headerResult)
            .withActivity(this)
            .withToolbar(toolbar)
            .addDrawerItems(
                item2,
                item3,
                item4
            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*,*>): Boolean {
                    if(position == 1) {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    else if (position == 2){
                        if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED ){
                            val intent = Intent(activity,TrackingService::class.java)
                            startService(intent)
                        }else ActivityCompat.requestPermissions(this@MainActivity,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

                    }
                    else if (position == 3){
                        val intent = Intent(activity,TrackingService::class.java)
                        stopService(intent);
                    }
                    return false
                }
            })
            .build()

    }

    private fun startService() {
        var serviceIntent  = Intent(this,NotificationService::class.java);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");

        //ContextCompat.startForegroundService(this, serviceIntent);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this,TrackingService::class.java)
                    startService(intent)
                } else {
                    Toast.makeText(this@MainActivity, "Please grant location permission to start Away mode ", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

}