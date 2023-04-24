package com.example.myapplication.models

import java.util.*

class ChatMessage {
    lateinit var senderId: String
    lateinit var receiverId: String
    lateinit var message: String
    lateinit var dateTime: String
    var dateObject = Date()
    lateinit var conversionId: String
    lateinit var conversionName: String
    lateinit var conversionImage: String
}