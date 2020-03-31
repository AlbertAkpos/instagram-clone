package com.example.instagramclone.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.Fragments.ProfileFragment
import com.example.instagramclone.model.User
import com.example.instagramclone.R
import com.example.instagramclone.utillity.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserAdapter(private val context: Context,
                  private val usersList: List<User>,
                  private val isFragment: Boolean=false): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val firebaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("User")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item_layout, parent, false))
    }

    override fun getItemCount() = usersList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = usersList[position]

        holder.fullname.text = user.fullname
        holder.username.text = user.username

        Glide.with(context).asBitmap()
            .load(user.image)
            .error(R.drawable.profile)
            .into(holder.profileImage)



        currUserCantFollowItSelf(user, holder)

        checkFollowingStatus(user.uid, holder.followBtn)

        //onClick Listener for displayed profiles
        holder.itemView.setOnClickListener {view ->
            goToProfileFragment(user, view)

        }

        //Set onClick for the follow button
        holder.followBtn.setOnClickListener {
            val loggedInUserId = firebaseUser?.uid.toString()
            if(holder.followBtn.text.toString() == "Follow"){
                Util.followUser(loggedInUserId, user)
            } else {
                Util.unFollowUser(loggedInUserId, user)
            }
        }
    }

    class UserViewHolder(view: View): RecyclerView.ViewHolder(view){
        val fullname = view.findViewById<TextView>(R.id.fullname_search)
        val username = view.findViewById<TextView>(R.id.username_search)
        val profileImage = view.findViewById<ImageView>(R.id.profile_image_search)
        val followBtn = view.findViewById<Button>(R.id.follow_btn)

    }


    fun checkFollowingStatus(uid:String, followingBtn: Button) {
        //get following reference
        val followRef = firebaseUser?.uid.let { id ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(id.toString())
                .child("Following")
        }

        followRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followingBtn.text = if(dataSnapshot.child(uid).exists()) "Following" else "Follow"

            }
        })
    }


    private fun currUserCantFollowItSelf(user: User, holder: UserViewHolder){
        firebaseRef.child(firebaseUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("fullname").value.toString()
                if (name == user.fullname){
                    holder.followBtn.visibility = View.INVISIBLE
                } else {
                    holder.followBtn.visibility = View.VISIBLE
                }
            }
        })
    }


    private fun goToProfileFragment(user: User, view: View){
        val profileFragment = ProfileFragment()

        val bundle = Bundle()
        bundle.putParcelable("user", user)
        profileFragment.arguments = bundle

        if (user.uid == firebaseUser?.uid)
            view.findNavController().navigate(R.id.profile)
        else
            view.findNavController().navigate(R.id.profile, bundle)


    }
}