package com.example.instagramclone.utillity

import android.app.ProgressDialog
import android.content.Context
import android.view.View
import com.example.instagramclone.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.view.*

object Util {
    private lateinit var user: User
    private lateinit var progressbar: ProgressDialog
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
                        .setValue(true)
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
                        .removeValue()
                }
            }
    }

     fun getFollowers(view: View, uid: String ){
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(uid)
                .child("Followers")


        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    view?.number_of_followers?.text = dataSnapshot.childrenCount.toString()
                }
            }
        })
    }

    fun getFollowing(view: View, uid: String){
        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(uid)
                .child("Following")


        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    view.number_of_following.text = dataSnapshot.childrenCount.toString()
                }
            }
        })
    }



    fun showProgressBar(context: Context) {
        progressbar = ProgressDialog(context)
        progressbar.setTitle("Account settings")
        progressbar.setMessage("Please wait...")
        progressbar.show()
    }

    fun dismissProgressBar() = progressbar.dismiss()




}