package com.example.messengerapp

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class RegisterActivity : AppCompatActivity() {

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

        photoSelector.setOnClickListener {
            Log.d("RegisterActivity", "pressed image")


            // Listen for an intent to pick an action and set that action to select only images
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            // we search for the image result, and we make a request to the onActivityResult function, to give me the image
            startActivityForResult(intent, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was...
            Log.d("RegisterActivity", "This is the image")

            // this return a URI, that represents the location of where that image is stored in the device
            val uri = data.data
            Log.d("RegisterActivity", "Print URI: $uri")
            // with bitmap we have access to the bitmap of the photo that is selected.
//            val source = ImageDecoder.createSource(contentResolver, uri!!)
//            val bitmap = ImageDecoder.decodeDrawable(source)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            Log.d("RegisterActivity", "Print bitmap/source: $bitmap")
            photoSelector.background = bitmapDrawable

        } else {
            Log.w("RegisterActivity", "Failed to load")
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
