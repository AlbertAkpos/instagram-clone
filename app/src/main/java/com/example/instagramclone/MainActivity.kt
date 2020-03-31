package com.example.instagramclone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.instagramclone.services.CustomEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus


class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_controller)

        bottom_navigation.setupWithNavController(navController)


    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        println("""
            
           main activity 
            
        """)


        if (data != null) {
            println("""
                
               Data is not null 
                
            """)
            EventBus.getDefault().post(CustomEvent(requestCode,
                resultCode, data))
        }


    }




    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }



}