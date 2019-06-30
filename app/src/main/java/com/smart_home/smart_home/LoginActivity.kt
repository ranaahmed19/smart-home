package com.smart_home.smart_home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    private var email: String? = null
    private var password: String? = null

    private var ForgotPassword: TextView? = null
    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnCreateAccount: Button? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        ForgotPassword = findViewById<View>(R.id.btn_reset_password)as TextView
        inputEmail = findViewById<View>(R.id.email) as EditText
        inputPassword = findViewById<View>(R.id.password) as EditText
        btnLogin = findViewById<View>(R.id.btn_login) as Button
        btnCreateAccount = findViewById<View>(R.id.btn_signup) as Button
        auth = FirebaseAuth.getInstance()
        val user = auth!!.currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        ForgotPassword!!
            .setOnClickListener {
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            }

        btnCreateAccount!!
            .setOnClickListener { startActivity(Intent(this@LoginActivity,
                SignupActivity::class.java)) }

        btnLogin!!.setOnClickListener{ logInUser() }

    }

    private fun logInUser(){
        email = inputEmail?.text.toString()
        password = inputPassword?.text.toString()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            auth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        updateUI()
                    }
                }
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
