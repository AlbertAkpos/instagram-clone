package com.example.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.multidex.MultiDex
import com.example.instagramclone.Fragments.HomeFragment
import com.example.instagramclone.Fragments.NotificationFragment
import com.example.instagramclone.Fragments.ProfileFragment
import com.example.instagramclone.Fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
     private val navigationSelectedListner = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home_icon -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true

            }

            R.id.add_post -> {
                return@OnNavigationItemSelectedListener true
            }

            R.id.notification -> {
                moveToFragment(NotificationFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true

            }
        }

         false


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moveToFragment(HomeFragment())
        bottom_navigation.setOnNavigationItemSelectedListener(navigationSelectedListner)
    }


    private fun moveToFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}
