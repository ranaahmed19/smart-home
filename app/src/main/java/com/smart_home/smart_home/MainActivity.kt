package com.smart_home.smart_home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (supportActionBar != null)
            supportActionBar?.hide()
        var room1Button = findViewById<Button>(R.id.room1BT) as FrameLayout
        var room2Button = findViewById<Button>(R.id.room2BT) as FrameLayout
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
            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent);
        }
        startService()
    }

    private fun startService() {
        var serviceIntent  = Intent(this,NotificationService::class.java);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");

        ContextCompat.startForegroundService(this, serviceIntent);
    }

}