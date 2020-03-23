package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main_user.*

class UserActivity: AppCompatActivity() {
    private var array = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user)

        verifyUserIsLoggedIn()
    }
    private val uid = FirebaseAuth.getInstance().uid

    private fun verifyUserIsLoggedIn() {
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            getUserData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_meniu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getUserData() {

        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("users/$uid")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.w("User", "Failed to retrieve data", error.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val username = dataSnapshot.child("username").getValue()
//                val profileImageUrl = dataSnapshot.child("profileImageUrl").getValue()
                Log.d("User", "Username: $username")
            }

        })

    }
}
