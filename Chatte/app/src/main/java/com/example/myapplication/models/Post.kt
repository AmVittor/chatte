package com.example.myapplication.models

import android.os.Parcel
import android.os.Parcelable

class Post() : Parcelable {
    var description: String = ""
    var imagePost: String = ""
    var imageProfile: String = ""
    var name: String = ""

    constructor(parcel: Parcel) : this() {
        description = parcel.readString() ?: ""
        imagePost = parcel.readString() ?: ""
        imageProfile = parcel.readString() ?: ""
        name = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeString(imagePost)
        parcel.writeString(imageProfile)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}