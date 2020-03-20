package com.example.messengerapp

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

    private var selectedPhotoUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was...
            Log.d("RegisterActivity", "This is the image")

            // this return a URI, that represents the location of where that image is stored in the device
            selectedPhotoUri = data.data
            Log.d("RegisterActivity", "Print URI: $selectedPhotoUri")
            // with bitmap we have access to the bitmap of the photo that is selected.
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
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

        Log.d("RegisterActivity", "This is the mail: $email")
        Log.d("RegisterActivity", "This is the pw: $password")

        // Firebase auth
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("RegisterActivity", "createUserWithEmail: success")
                val user = auth.currentUser
                uploadImageToFirebaseStorage()
            } else {
                Log.w("RegisterActivity", "createUserWithEmail: failed", it.exception)
                Toast.makeText(baseContext, "The email address is already in use!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.d("RegisterActivity", "Failed to create user: ${it.message}")
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val storage = FirebaseStorage.getInstance()
        val ref = storage.getReference("images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("RegisterActivity", "Upload the photo: ${it.metadata?.path}")

            // Getting access to the download location of the photo
            ref.downloadUrl.addOnSuccessListener {
                Log.d("RegisterActivity", "Photo download url: $it")

                saveUserToFirebaseDatabase(it.toString())
            }.addOnFailureListener {
                Log.w("RegisterActivity", "Couldn't download the url")
            }
        }.addOnFailureListener {
            Log.w("RegisterActivity", "Couldn't upload the photo")
        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: "" // elvis operator to default it if null to empty string.
        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("/users/${uid}")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)
        ref.setValue(user).addOnSuccessListener {
            Log.d("RegisterActivity", "We saved the user to firebase db $uid and user $user")
        }.addOnFailureListener {
            Log.w("RegisterActivity", "Problem saving the user to the FB DB")
        }
    }
}


class User(val uid: String ,val username: String, val profileImageUrl: String) {

}