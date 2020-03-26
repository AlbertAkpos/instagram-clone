package com.example.instagramclone.utillity

import android.view.View
import com.example.instagramclone.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.view.*

object Util {
    fun followUser(loggedInUserId: String, otherUser: User) {
        FirebaseDatabase.getInstance().reference
            .child("Follow").child(loggedInUserId)
            .child("Following").child(otherUser.uid)
            .setValue(true)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(otherUser.uid)
                        .child("Followers").child(loggedInUserId)
                        .setValue(true).addOnCompleteListener {task ->
                            if(task.isSuccessful){

                            }
                        }
                }
            }
    }


    fun unFollowUser(loggedInUserId: String, otherUser: User){
        FirebaseDatabase.getInstance().reference
            .child("Follow").child(loggedInUserId)
            .child("Following").child(otherUser.uid)
            .removeValue()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(otherUser.uid)
                        .child("Followers").child(otherUser.uid)
                        .removeValue().addOnCompleteListener {task ->
                            if(task.isSuccessful){

                            }
                        }
                }
            }
    }

     fun getFollowers(view: View){
        val followersRef = FirebaseAuth.getInstance().currentUser?.uid.let { uid->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(uid.toString())
                .child("Followers")
        }

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    view?.number_of_followers?.text = dataSnapshot.childrenCount.toString()
                }
            }
        })
    }

    fun getFollowing(view: View){
        val followingRef = FirebaseAuth.getInstance().currentUser?.uid.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(uid.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    view.number_of_following.text = dataSnapshot.childrenCount.toString()
                }
            }
        })
    }
}