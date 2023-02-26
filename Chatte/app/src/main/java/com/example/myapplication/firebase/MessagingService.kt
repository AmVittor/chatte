package com.example.myapplication.firebase

import android.util.Log
import androidx.annotation.NonNull
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService: FirebaseMessagingService() {


    override fun onNewToken(@NonNull token: String){
        super.onNewToken(token)

    }

     override fun onMessageReceived(@NonNull remoteMessage: RemoteMessage){
        super.onMessageReceived(remoteMessage)

    }
}