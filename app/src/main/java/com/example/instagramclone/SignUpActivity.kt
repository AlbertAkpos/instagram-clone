package com.example.instagramclone

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap


class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        sign_in_link_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }


        sign_up_btn.setOnClickListener {
            createAccount(it)
        }

    }


    fun createAccount(view: View){

        //Bind this in my xml
        val fullName = fullname.text.toString()
        val userName = username.text.toString()
        val email = email_sign_in.text.toString()
        val password = password_sign_in.text.toString()


        when{
            TextUtils.isEmpty(fullName) -> Snackbar.make(view, "Please fill in a fullname", Snackbar.LENGTH_LONG ).show()
            TextUtils.isEmpty(userName) -> Snackbar.make(view, "Please fill in a username", Snackbar.LENGTH_LONG ).show()
            TextUtils.isEmpty(email) -> Snackbar.make(view, "Please fill in a email", Snackbar.LENGTH_LONG ).show()
            TextUtils.isEmpty(password) -> Snackbar.make(view, "Please fill in a password", Snackbar.LENGTH_LONG ).show()
            else -> {
                //Setting a progress dialogue
                val layout = findViewById<RelativeLayout>(R.id.sign_up_layout)
                val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleLarge)
                progressBar.isIndeterminate = true

                val params = RelativeLayout.LayoutParams(100, 100)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                layout.addView(progressBar, params)


                val mAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Snackbar.make(view, "Task successful", Snackbar.LENGTH_LONG ).show()
                            saveUserInfo(fullName, userName, email, view, progressBar)

                        } else  {
                            //If authentication is not successful
                            val message = task.exception.toString()
                            mAuth.signOut()
                            Snackbar.make(view, "Error: $message", Snackbar.LENGTH_LONG ).show()
                            progressBar.visibility = View.GONE
                        }
                    }
            }
        }

    }


    private fun saveUserInfo(fullname: String, userName: String, email: String, view: View, progressBar: ProgressBar){

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("User")
        var userMap = HashMap<String, Any>()

        userMap["uid"] = currentUserId!!
        userMap["fullname"] = fullname
        userMap["lowerCaseName"] = fullname.toLowerCase(Locale.ROOT)
        userMap["username"] = userName
        userMap["email"] = email
        userMap["bio"] = "Hey there, I'm using Jodava"
        userMap["image"] = "gs://instagram-clone-d1306.appspot.com/Default Images/profile.png"

        FirebaseDatabase.getInstance().reference.child("Follow").child(currentUserId.toString())
            .child("Following")
            .child(currentUserId.toString())
            .setValue(true)

        userRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {

                    Snackbar.make(view, "Account created successfully", Snackbar.LENGTH_LONG ).show()

                    val intent = Intent(this, MainActivity::class.java)
                    //SO THE USER CANNOT GO BACK TO SIGN UP PAGE IF HE DOESN'T LOG OUT
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else  {
                    //show error message is there is an error
                    val message = task.exception.toString()
                    FirebaseAuth.getInstance().signOut()
                    Snackbar.make(view, "Error: Unable to sign in at this time", Snackbar.LENGTH_LONG ).show()
                    progressBar.visibility = View.GONE

                }
            }

    }
}
