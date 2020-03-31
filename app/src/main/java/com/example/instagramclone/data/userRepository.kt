package com.example.instagramclone.data

import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class userRepository {
    lateinit var loggedInUser: User

    private fun getUser(): User {
        val id = FirebaseAuth.getInstance().currentUser?.uid
        val firebaseUserRef = FirebaseDatabase.getInstance().reference.child("User").child(id.toString())
        firebaseUserRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    loggedInUser = dataSnapshot.getValue(User::class.java)!!
                }
            }
        })

        return loggedInUser
    }



}