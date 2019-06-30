package com.smart_home.smart_home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase

class ActionReceiver : BroadcastReceiver() {

    private var db = FirebaseDatabase.getInstance()
    private var reference = db.getReference("Notifications")
    private var requests = reference.child("FaceDetected")

    override fun onReceive(p0: Context?, p1: Intent?) {
        var action = p1?.getStringExtra("action")
        var user = p1?.getStringExtra("User").toString()
        if(action == "Accept") {
            requests.child(user).setValue("Accept")
        }else if(action == "Reject"){
            requests.child(user).setValue("Reject")
        }
        var it : Intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        p0!!.sendBroadcast(it)
    }
}
