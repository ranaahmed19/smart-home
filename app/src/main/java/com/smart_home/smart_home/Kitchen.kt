package com.smart_home.smart_home

import android.graphics.Color.rgb
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Kitchen : AppCompatActivity() {

    private var foodDatabase = FirebaseDatabase.getInstance().getReference("Food")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitchen)
        val table = findViewById<TableLayout>(R.id.foodTable)
        foodDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var foodTableContent : HashMap<String,String> = dataSnapshot.getValue() as (HashMap<String, String>)
                if (table.childCount >1)
                    table.removeViews(1,table.childCount)
                if( foodTableContent!!.size>0)
                for ((key, value) in foodTableContent!!) {
                    val row = TableRow(this@Kitchen)
                    val c1 = TextView(this@Kitchen)
                    val c2 = TextView(this@Kitchen)
                    c1.setText(key!!.toString())
                    c1.height= 100
                    c2.height = 100
                    c1.setBackgroundResource(R.drawable.textview_boarder)
                    c2.setBackgroundResource(R.drawable.textview_boarder)
                    c1.setPadding(30,10,10,10)
                    c2.setPadding(30,10,10,10)

                    row.setBackgroundColor(rgb(211,211,211))
                    c2.setText(value!!.toString())
                    row.addView(c1)
                    row.addView(c2)
                    table.addView(row)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }
}
