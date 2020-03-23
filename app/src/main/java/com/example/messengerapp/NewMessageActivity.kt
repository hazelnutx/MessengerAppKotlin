package com.example.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

        recycler_view_new_message.adapter = adapter

        fetchUsers()
    }

    private fun fetchUsers() {
        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.w("NewMessage", "Failed to retrieve from db", error.toException())
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>() // add the adapter here to populate the View

                dataSnapshot.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java) // the user from RegisterActivity aka BluePrint
                    adapter.add(UserItem())
                }
                recycler_view_new_message.adapter = adapter
            }
        })
    }
}

class UserItem: Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}
