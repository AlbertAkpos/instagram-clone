package com.example.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_up_link_btn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }


        sign_in_btn.setOnClickListener { view ->
            val email = email_sign_in.text.toString()
            val password = password_sign_in.text.toString()

            when{
                TextUtils.isEmpty(email) -> Snackbar.make(view, "Please fill in a email", Snackbar.LENGTH_LONG ).show()
                TextUtils.isEmpty(password) -> Snackbar.make(view, "Please fill in a password", Snackbar.LENGTH_LONG ).show()
                else -> {
                    //Setting a progress dialogue
                    val layout = findViewById<RelativeLayout>(R.id.sign_in_layout)
                    val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleLarge)
                    progressBar.isIndeterminate = true

                    val params = RelativeLayout.LayoutParams(100, 100)
                    params.addRule(RelativeLayout.CENTER_IN_PARENT)
                    layout.addView(progressBar, params)


                    //Firebase authentication
                    val mAuth = FirebaseAuth.getInstance()
                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {task ->
                            if (task.isSuccessful){
                                progressBar.visibility = View.GONE

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

        }

    }


    override fun onStart() {
        super.onStart()
        //Check if the user has login in before. The direct the person to MainActivity
        if (FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            //SO THE USER CANNOT GO BACK TO SIGN UP PAGE IF HE DOESN'T LOG OUT
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
