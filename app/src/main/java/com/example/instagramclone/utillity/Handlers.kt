package com.example.instagramclone.utillity

import android.app.ProgressDialog
import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.theartofdev.edmodo.cropper.CropImage

class Handlers {

    fun openGallery(view: View) {
        CropImage.activity()
            .start(view.context as FragmentActivity)
    }
}