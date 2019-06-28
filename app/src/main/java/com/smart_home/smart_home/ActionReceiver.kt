package com.smart_home.smart_home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class ActionReceiver : BroadcastReceiver() {

    private var db = FirebaseDatabase.getInstance()
    private var reference = db.getReference("Notifications")
    private var requests = reference.child("FaceDetected")

    override fun onReceive(p0: Context?, p1: Intent?) {
        var action = p1?.getStringExtra("action")
        var user = p1?.getStringExtra("User").toString()
        if(action == "Accept") {
            //performAction1();
            Log.d("Etfadal ya " + p1?.getStringExtra("User"), ":)")
            requests.child(user).setValue("Accept")
        }else if(action == "Reject"){
            //performAction2();
            Log.d("3.oor Fe Dahya "+p1?.getStringExtra("User"),":(")
            requests.child(user).setValue("Reject")
        }
        //This is used to close the notification tray
        var it : Intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        p0!!.sendBroadcast(it)
    }
}
