package com.example.myapplication.listeners

import com.example.myapplication.models.User

interface UserListener {
    fun onUserClicked(user: User)

    fun initiateVideoMeeting(user: User)

    fun initiateAudioMetting(user: User)

}