package com.example.myapplication.utilities

class Constants {
    val KEY_IMAGE_URL = "messages/"
    val KEY_COLLECTION_USERS: String = "users"
    val KEY_NAME: String = "name"
    val KEY_EMAIL: String = "email"
    val KEY_PASSWORD: String = "password"
    val KEY_PREFERENCE_NAME: String = "chatAppPreference"
    val KEY_IS_SIGNED_IN: String = "isSignedIn"
    val KEY_USER_ID: String = "userId"
    val KEY_IMAGE: String = "image"
    val KEY_FCM_TOKEN: String = "fcmToken"
    val KEY_USER: String = "user"
    val KEY_COLLECTION_CHAT: String = "chat"
    val KEY_SENDER_ID: String = "senderId"
    val KEY_RECEIVER_ID: String = "receiverId"
    val KEY_MESSAGE: String = "message"
    val KEY_TIMESTAMP: String = "timeStamp"
    val KEY_COLLECTION_CONVERSATIONS: String = "conversations"
    val KEY_SENDER_NAME: String = "senderName"
    val KEY_RECEIVER_NAME: String = "receiverName"
    val KEY_SENDER_IMAGE: String = "receiverImage"
    val KEY_RECEIVER_IMAGE: String = "receiverImage"
    val KEY_LAST_MESSAGE: String = "lastMessage"
    val KEY_AVAILABILITY: String = "availability"
    val REMOTE_MSG_AUTHORIZATION = "Authorization"
    val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
    val REMOTE_MSG_DATA = "data"
    val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

    val KEY_COLLECTION_POST: String = "posts"
    val KEY_DESCRIPTION: String = "description"
    val KEY_IMAGE_POST: String = "imagePost"


    var remoteMsgHeaders: HashMap<String, String>? = null

    fun getRemoteMsgHeaders2(): HashMap<String, String>? {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = HashMap()
            remoteMsgHeaders!![REMOTE_MSG_AUTHORIZATION] =
                "key=AAAAjpEfT2U:APA91bHkuvia7qoOMngDP8-2DB8RWrFxip_eNlqq6J6iiBPThQGlAqWgT1BWtlgVc9x3upgsYCJzR5_6kb5zmZPol8Gs_V00NkIQdaDP5-SW1I2L0ZirI8cWx5byfW8k5Kk-iEJLRJ8q"
        }
        remoteMsgHeaders!![REMOTE_MSG_CONTENT_TYPE] = "application/json"
        return remoteMsgHeaders
    }

}