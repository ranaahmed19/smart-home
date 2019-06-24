package com.smart_home.smart_home

import android.app.*
import android.os.Build
import android.content.Intent
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NotificationService : Service() {

    val CHANNEL_ID = "ForegroundServiceChannel"

    private var reference = FirebaseDatabase.getInstance().getReference("Notifications")

    private var GasDatabase = reference.child("Gas")
    private var WaterDatabase = reference.child("Water")

    override fun onCreate() {
        super.onCreate()


    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        Log.d("notf","hereee")

        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )




        GasDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
               var notify = dataSnapshot.getValue() as (String)
                Log.d("change22",notify.toString())
                 val notification = NotificationCompat.Builder(this@NotificationService, CHANNEL_ID)
                    .setContentTitle("Foreground Service")
                    .setContentText("Water is leaking")
                    .setSmallIcon(R.drawable.ic_adb_user)
                    .setContentIntent(pendingIntent)
                    .build()
                with(NotificationManagerCompat.from(this@NotificationService)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(1, notification!!)
                }

                startForeground(1,notification!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("cancel","iii")

            }
        })




        //do heavy work on a background thread


        //stopSelf();

        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}