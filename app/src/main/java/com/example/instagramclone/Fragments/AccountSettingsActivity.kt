package com.example.instagramclone.Fragments


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.instagramclone.model.User

import com.example.instagramclone.R
import com.example.instagramclone.SignInActivity
import com.example.instagramclone.databinding.FragmentAccountSettingsBinding
import com.example.instagramclone.utillity.Util
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_account_settings.*

/**
 * A simple [Fragment] subclass.
 */
class AccountSettingsActivity : AppCompatActivity() {
    lateinit var binding: FragmentAccountSettingsBinding
    private val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var textChangedSatus = false
    private var imageUrl = ""
    private var imageUri: Uri? = null
    private var profilePicRef: StorageReference? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_account_settings)
        getUserInfo(firebaseUserId.toString())

        profilePicRef = FirebaseStorage.getInstance().reference.child("Profile pictures")

        binding.logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //Send the user to the sign in page
            val intent = Intent(this, SignInActivity::class.java)
            //SO THE USER CANNOT GO BACK TO SIGN UP PAGE IF HE DOESN'T LOG OUT
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textChangedSatus = true
            }
        }

        binding.changeProfilePhoto.setOnClickListener {
            CropImage.activity().setAspectRatio(1, 1)
                .start(this)
        }

        binding.fullname.addTextChangedListener(listener)
        binding.username.addTextChangedListener(listener)
        binding.bio.addTextChangedListener(listener)

        binding.saveProfileDetailsBtn.setOnClickListener{

            if(textChangedSatus){
                val fullname = binding.fullname.text.toString()
                val username = binding.username.text.toString()
                val bio = binding.bio.text.toString()

                when{
                    TextUtils.isEmpty(fullname) -> showError(binding.fullname)
                    TextUtils.isEmpty(username) -> showError(binding.fullname)
                    TextUtils.isEmpty(bio) -> showError(binding.fullname)
                    else   -> updateUserInfo(firebaseUserId.toString(), fullname, username, bio)
                }
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
        ){

            Util.showProgressBar(this)

            val result = CropImage.getActivityResult(data)
            imageUri = result.uri

            profile_image.setImageURI(imageUri)
            val profileImageRef = profilePicRef?.child(firebaseUserId.toString() + ".jpg")
            val uploadTask: StorageTask<*>
            uploadTask = profileImageRef!!.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful){
                    Util.showProgressBar(this)
                    task.exception.let {
                        throw it!!
                    }
                }

                return@Continuation profileImageRef.downloadUrl
            }).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                   Util.dismissProgressBar()

                    val downloadUrl = task.result.toString()
                    val userMap = HashMap<String, Any>()
                    userMap["image"] = downloadUrl

                    FirebaseDatabase.getInstance().reference.child("User").child(firebaseUserId.toString())
                        .updateChildren(userMap).addOnCompleteListener {task ->
                            if (task.isSuccessful){
                                showMessage("Profile picture updated")
                            } else {
                                Util.dismissProgressBar()
                            }
                        }

                } else {
                    Util.dismissProgressBar()
                    showMessage("An error occurred")

                }
            }
        }
    }

    private fun showMessage(message: String) {
         Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }


    private fun showError(editable: EditText) {
        editable.error = "Field cannot be empty"
    }

    private fun updateUserInfo(userId: String, fullname: String, username: String, bio: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("User").child(userId)
        val userMap = HashMap<String, Any>()

        userMap["fullname"] = fullname
        userMap["username"] = username
        userMap["bio"] = bio

        userRef.updateChildren(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful){
                showMessage("Account updated")
            } else {
                showMessage("Operation not successful. Please try again later.")
            }

        }

    }

    private fun getUserInfo(userId: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("User").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    val user = dataSnapshot.getValue(User::class.java)
                    Glide.with(this@AccountSettingsActivity).asBitmap()
                        .load(user?.image)
                        .error(R.drawable.profile_icon)
                        .into(binding.profileImage)
                    binding.fullname.setText(user?.fullname)
                    binding.username.setText(user?.username)
                    binding.bio.setText(user?.bio)

                }
            }
        })
    }




}
