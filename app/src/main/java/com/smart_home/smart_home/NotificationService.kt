package com.smart_home.smart_home

import android.app.*
import android.content.Context
import android.os.Build
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NotificationService : Service() {

    val CHANNEL_ID = "ForegroundServiceChannel"

    private var db1 = FirebaseDatabase.getInstance()//.getReference("Notifications")
    private var reference = db1.getReference("Notifications")

    private var waterReading : Double = 0.0
    private var gasReading : Double = 0.0

    override fun onCreate() {
        super.onCreate()

        startForeground(10021,NotificationCompat.Builder(this@NotificationService,CHANNEL_ID).build())

        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val cont : Context = this

        val intentAccept = Intent(this,ActionReceiver::class.java)
        intentAccept.putExtra("action","Accept")

        val intentReject = Intent(this,ActionReceiver::class.java)
        intentReject.putExtra("action","Reject")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                createNotificationChannel()
                dataSnapshot!!.children.forEach {
                    if (it.key == "Water") {

                        if (waterReading != it.value) {
                            val waterNotification = NotificationCompat.Builder(this@NotificationService, CHANNEL_ID)
                                .setContentTitle("Alert: Water Leakage!!")
                                .setContentText(it.value.toString())
                                .setSmallIcon(R.drawable.ic_adb_user)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build()

                            //startForeground(1,waterNotification!!)
                            with(NotificationManagerCompat.from(this@NotificationService)) {
                                // notificationId is a unique int for each notification that you must define
                                notify(1, waterNotification!!)
                            }
                            waterReading = it.value as (Double)
                        }
                    } else if (it.key == "Gas") {

                        if (gasReading != it.value) {
                            val gasNotification = NotificationCompat.Builder(this@NotificationService, CHANNEL_ID)
                                .setContentTitle("Alert: Gas Leakage!!")
                                .setContentText(it.value.toString())
                                .setSmallIcon(R.drawable.ic_adb_user)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                //.addExtras(it.value.toString())
                                .build()
                            //startForeground(2,gasNotification!!)
                            with(NotificationManagerCompat.from(this@NotificationService)) {
                                // notificationId is a unique int for each notification that you must define
                                notify(2, gasNotification!!)
                            }
                            gasReading = it.value as (Double)
                        }
                    } else if (it.key == "FaceDetected") {
                        var notifId = 100
                        var reqCode = 1000
                        it.children.forEach {
                            Log.d("Value", it.value.toString())
                            if (it.value.toString() == "Request") {
                                val pIntentR = PendingIntent.getBroadcast(
                                    cont,
                                    reqCode++,
                                    intentReject,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                                )
                                val pIntentA = PendingIntent.getBroadcast(
                                    cont,
                                    reqCode++,
                                    intentAccept,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                                )

                                intentAccept.putExtra("User", it.key.toString())
                                intentReject.putExtra("User", it.key.toString())




                                val reqNotification = NotificationCompat.Builder(this@NotificationService, CHANNEL_ID)
                                    .setContentTitle("Request for Permission")
                                    .setContentText(it.key.toString() + " Wants to Enter")
                                    .setSmallIcon(R.drawable.ic_adb_user)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .addAction(R.drawable.done, "Accept", pIntentA)
                                    .addAction(R.drawable.clear, "Deny", pIntentR)
                                    .build()

                                //startForeground(3,reqNotification!!)
                                with(NotificationManagerCompat.from(this@NotificationService)) {
                                    // notificationId is a unique int for each notification that you must define
                                    notify(notifId++, reqNotification!!)
                                }
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("cancel", "iii")

            }
        })
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