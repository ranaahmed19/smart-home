package com.smart_home.smart_home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private val TAG = "ForgotPasswordActivity"

    private var inputEmail: EditText? = null
    private var btnSubmit: Button? = null
    private var btnBack: Button? = null

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        inputEmail = findViewById<View>(R.id.email) as EditText
        btnSubmit = findViewById<View>(R.id.btn_reset_password) as Button
        btnBack = findViewById<View>(R.id.btn_back) as Button
        auth = FirebaseAuth.getInstance()

        btnBack!!
            .setOnClickListener{
                startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
            }
        btnSubmit!!.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail(){
        val email = inputEmail?.text.toString()
        if (!TextUtils.isEmpty(email)) {
            auth!!
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val message = "Email sent."
                        updateUI()
                    }
                }
        }
    }
    private fun updateUI() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
