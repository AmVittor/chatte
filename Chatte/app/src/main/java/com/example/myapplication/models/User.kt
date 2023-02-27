package com.example.myapplication.models

import android.os.Parcel
import android.os.Parcelable

class User() : Parcelable {
    var name: String = ""
    var image: String = ""
    var email: String = ""
    var token: String = ""
    var id: String = ""

    constructor(parcel: Parcel) : this() {
        name = parcel.readString() ?: ""
        image = parcel.readString() ?: ""
        email = parcel.readString() ?: ""
        token = parcel.readString() ?: ""
        id = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(email)
        parcel.writeString(token)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
