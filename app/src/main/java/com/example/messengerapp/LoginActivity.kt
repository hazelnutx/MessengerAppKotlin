package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            performLogin()
        }

        back_to_register_textview_login.setOnClickListener {
            finish()
        }
    }

    private fun performLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        // Check if inputs are empty.
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email/password", Toast.LENGTH_SHORT).show()
            return
        }


        Log.d("Login", "Logged in with: $email, $password")

        // Firebase Auth.
        val auth = FirebaseAuth.getInstance()
        // Firebase DB
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = auth.currentUser
                Log.d("Login", "logInWithEmail: success with ${user!!.uid}")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                startActivity(intent)

            } else {
                Log.w("Login", "logInWithEmail: failed", it.exception)
                Toast.makeText(baseContext, "LogIn Failed", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.d("Login", "Failed to login: ${it.message}")
        }
    }
}