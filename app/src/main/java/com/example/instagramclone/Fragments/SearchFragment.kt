package com.example.instagramclone.Fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Adapter.UserAdapter
import com.example.instagramclone.Model.User

import com.example.instagramclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var usersList: MutableList<User>? = null
    private var adapter: UserAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.search_recycler_view
//        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        usersList = ArrayList()
        adapter = UserAdapter(context!!, usersList as ArrayList, true)

        recyclerView?.adapter = adapter


        view.search_users_input.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recyclerView?.visibility = View.VISIBLE
                getUsersFromFirebase()

                //function that do the actual searching
                searchUser(s.toString().trim())
            }
        })


        return view
    }


    private fun getUsersFromFirebase() {
        val userRef = FirebaseDatabase.getInstance().reference.child("User")
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(view?.search_users_input?.text.toString()  == ""){
                    usersList?.clear()

                    for (snapShot in dataSnapshot.children){
                        val user = snapShot.getValue(User::class.java)
                        if(user != null) usersList?.add(user)
                    }

                    adapter?.notifyDataSetChanged()

                }

            }
        })
    }

    private fun searchUser(text: String) {
        val query = FirebaseDatabase.getInstance().reference
            .child("User")
            .orderByChild("fullname")
            .startAt(text).endAt(text + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList?.clear()

                for(snapshot in dataSnapshot.children){
                    val user = snapshot.getValue(User::class.java)
                    if(user != null) usersList?.add(user)
                }

                if(text.isEmpty()) usersList?.clear()

                adapter?.notifyDataSetChanged()

            }
        })
    }


}
