package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text_view.setOnClickListener {
            Log.d("RegisterToLogin", "Try to show log in activity")

            // Watch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill the email / password text field!", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Register", "This is the mail: $email")
        Log.d("Register", "This is the pw: $password")

        // Firebase auth
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Auth", "createUserWithEmail: success")
                val user = auth.currentUser

            } else {
                Log.w("Auth", "createUserWithEmail: failed", it.exception)
                Toast.makeText(baseContext, "Auth failed", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.d("Auth", "Failed to create user: ${it.message}")
        }
    }
}
