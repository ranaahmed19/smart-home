package com.smart_home.smart_home

import android.content.Intent
import android.os.Bundle

import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignupActivity : AppCompatActivity() {

    private var inputFirstName: EditText? = null
    private var inputLastName: EditText? = null
    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null

    private val TAG = "CreateAccountActivity"

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null

    private var btnSignIn: Button? = null
    private var btnSignUp: Button? = null

    private var auth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        inputFirstName = findViewById<View>(R.id.first_name) as EditText
        inputLastName = findViewById<View>(R.id.last_name) as EditText
        inputEmail =  findViewById<View>(R.id.email) as EditText
        inputPassword =  findViewById<View>(R.id.password) as EditText
        btnSignIn =  findViewById<View>(R.id.sign_in_button) as Button
        btnSignUp =  findViewById<View>(R.id.sign_up_button) as Button
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        auth = FirebaseAuth.getInstance()

        btnSignIn!!.setOnClickListener{
             startActivity(Intent(this@SignupActivity,
                    LoginActivity::class.java))
        }

        btnSignUp!!.setOnClickListener{ createNewAccount() }
    }

    private fun createNewAccount() {
        firstName = inputFirstName?.text.toString()
        lastName = inputLastName?.text.toString()
        email = inputEmail?.text.toString()
        password = inputPassword?.text.toString()

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
            && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
        }

        auth!!
            .createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth!!.currentUser!!.uid
                    verifyEmail()
                    val currentUserDb = mDatabaseReference!!.child(userId)
                    currentUserDb.child("firstName").setValue(firstName)
                    currentUserDb.child("lastName").setValue(lastName)
                    updateUserInfoAndUI()
                }
            }
    }

    private fun updateUserInfoAndUI() {
        val intent = Intent(this@SignupActivity, MapsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {
        val mUser = auth!!.currentUser
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                }
            }
    }
}
