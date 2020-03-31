package com.example.instagramclone.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Adapter.PostAdapter
import com.example.instagramclone.model.Post

import com.example.instagramclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var posts: ArrayList<Post>
    lateinit var postAdapter: PostAdapter
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        layoutManager = LinearLayoutManager(context)
        posts = ArrayList()
        postAdapter = PostAdapter(posts, context!!)
        recyclerView = view.post_feed_recycler_view
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = postAdapter

        getFollowingsList()


        return view
    }

    private fun getFollowingsList() {
        val followingList = ArrayList<String>()
        val followingsRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(currentUserId.toString())
            .child("Following")

        followingsRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    followingList.clear()
                    for (snapShot in dataSnapshot.children){
                        followingList.add(snapShot.key!!)
                    }

                    getPostByFollowings(followingList)
                }
            }
        })
    }

    private fun getPostByFollowings(followingList: java.util.ArrayList<String>) {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        posts.clear()
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
               for (snapShot in dataSnapshot.children){
                   val post = snapShot.getValue(Post::class.java)
                   for (publisherId in followingList){
                       if (publisherId == post?.publisher){
                           posts.add(post)
                       }
                   }
               }
            }
        })

        postAdapter.notifyDataSetChanged()
    }


}
