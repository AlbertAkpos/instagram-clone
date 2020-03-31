package com.example.instagramclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.model.Post
import com.example.instagramclone.model.User
import com.example.instagramclone.R
import com.example.instagramclone.databinding.PostItemLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostAdapter(private val posts: List<Post>, private val context: Context): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = DataBindingUtil.inflate<PostItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.post_item_layout, parent, false)



        return  PostViewHolder(binding)
    }

    override fun getItemCount() = posts.size
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        val post = posts[position]
        val postOwner = getPublisherInfo(post.publisher)
        holder.bind(post, postOwner)

    }

    private fun getPublisherInfo(publisherId: String): User {
        val firebaseRef = FirebaseDatabase.getInstance().reference.child("User").child(publisherId)
        var postOwner: User? = User()
        firebaseRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    postOwner = dataSnapshot.getValue(User::class.java)
                }
            }
        })

        return postOwner!!
    }

    inner class PostViewHolder(private val binding: PostItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post, postOwner: User){
            binding.post = post
            binding.postOwner = postOwner

            Glide.with(context).asBitmap()
                .load(postOwner.image)
                .error(R.drawable.profile_icon)
                .into(binding.postOwnerImage)

            Glide.with(context).asBitmap()
                .load(post.image)
                .error(R.drawable.profile)
                .into(binding.postImageHome)
        }
    }
}