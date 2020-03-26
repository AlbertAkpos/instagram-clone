package com.example.instagramclone.Fragments


import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramclone.MainActivity

import com.example.instagramclone.R
import com.example.instagramclone.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account_settings.*

/**
 * A simple [Fragment] subclass.
 */
class AccountSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_account_settings)

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //Send the user to the sign in page
            val intent = Intent(this, SignInActivity::class.java)
            //SO THE USER CANNOT GO BACK TO SIGN UP PAGE IF HE DOESN'T LOG OUT
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }




}
