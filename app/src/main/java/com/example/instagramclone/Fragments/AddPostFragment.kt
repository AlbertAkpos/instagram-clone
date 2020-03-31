package com.example.instagramclone.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.instagramclone.R
import com.example.instagramclone.databinding.FragmentAddPostBinding
import com.example.instagramclone.services.CustomEvent
import com.example.instagramclone.utillity.Handlers
import com.example.instagramclone.utillity.Util
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


const val POST_PICS = "Post pictures"
class AddPostFragment : Fragment() {
    private var imageUri: Uri? = null

    private var storageRef: StorageReference? = null

    private lateinit var binding: FragmentAddPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_post, container, false)

        val handler = Handlers()
        binding.handlers = handler

        storageRef = FirebaseStorage.getInstance().reference.child(POST_PICS)

        binding.addPostBtn.setOnClickListener{
            if(imageUri != null)
                uploadPost()
            else Snackbar.make(binding.root, "Please select an image", Snackbar.LENGTH_LONG).show()

        }
        return binding.root
    }

    private fun uploadPost() {
        Util.showProgressBar(context!!)


        val fileRef = storageRef?.child(imageUri?.lastPathSegment.toString())
        val uploadTask =
            fileRef?.putFile(imageUri!!)
                ?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

                    if(!task.isSuccessful){
                        Util.dismissProgressBar()
                        Snackbar.make(binding.root, "Could not upload picture", Snackbar.LENGTH_LONG).show()
                    }

                    return@Continuation fileRef?.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        val databaseRef = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postMap = HashMap<String, Any>()
                        val postId = databaseRef.push().key // generates random key
                        postMap["postId"] = postId!!
                        postMap["description"] = binding.postDescription.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        postMap["image"] = downloadUrl.toString()

                        databaseRef.child(postId).updateChildren(postMap).addOnCompleteListener {
                            if (task.isSuccessful){
                                findNavController().navigate(R.id.home_icon)
                                Util.dismissProgressBar()
                            }
                        }
                    }

                }

    }




    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun customEvent(event: CustomEvent){
        onActivityResult(event.requestCode, event.resultCode, event.data)
    }








    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

       if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
           resultCode == Activity.RESULT_OK &&
           data != null) {
           val result = CropImage.getActivityResult(data)
           imageUri = result.uri
           binding.imagePost.setImageURI(imageUri)
       }
    }



}