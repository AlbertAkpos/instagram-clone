package com.example.instagramclone.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.instagramclone.model.User

import com.example.instagramclone.R
import com.example.instagramclone.utillity.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.fullname
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private val userId  = FirebaseAuth.getInstance().currentUser?.uid
    lateinit var user: User
    private val firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = User()
        val bundle: User? = arguments?.getParcelable("user")

        if(bundle != null){
            user = bundle
            fullname.text = user.fullname
            profile_bio.text = user.bio
            checkFollowAndFollowing(user, view)

            Util.getFollowers(view, user.uid)
            Util.getFollowing(view, user.uid)
            getUserInfo(user.uid)

            view.edit_follow_btn.setOnClickListener {
                if (view.edit_follow_btn.text == "Follow"){
                    Util.followUser(firebaseUser?.uid.toString(), user)
                } else {
                    Util.unFollowUser(firebaseUser?.uid.toString(), user)
                }

            }

        } else {
            edit_follow_btn.text = "Edit profile"
            Util.getFollowers(view, userId.toString())
            Util.getFollowing(view, userId.toString())
            getUserInfo(userId.toString())
            view.edit_follow_btn.setOnClickListener {
                startActivity(Intent(context, AccountSettingsActivity::class.java))
            }
        }
    }


    private fun checkFollowAndFollowing(user: User, view: View) {
        val followRef = firebaseUser?.uid.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(uid.toString())
                .child("Following")
        }

            followRef.addValueEventListener( object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.child(user.uid).exists()) {
                        view.edit_follow_btn.text = "Following"
                    } else {
                        view.edit_follow_btn.text = "Follow"

                    }
                }
            })
    }


    private fun getUserInfo(userId: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("User").child(userId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    val user = dataSnapshot.getValue(User::class.java)
                    Glide.with(context!!).asBitmap()
                        .load(user?.image)
                        .error(R.drawable.profile_icon)
                        .into(profile_image)

                    fullname.text = user?.fullname
                    profile_bio.text = user?.bio

                }
            }
        })
    }




}
