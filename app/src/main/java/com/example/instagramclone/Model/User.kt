package com.example.instagramclone.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
 data class User  (
    var bio: String? = null,
    var email: String,
    var fullname: String,
    var image: String?=null,
    var uid: String,
    var username: String
            ): Parcelable{
     constructor(): this("", "", "", "", "", "")

 }

