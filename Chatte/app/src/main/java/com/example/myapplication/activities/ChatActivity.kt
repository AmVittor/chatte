package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityChatBinding
import com.example.myapplication.databinding.ActivitySignInBinding
import com.example.myapplication.models.User
import com.example.myapplication.utilities.Constants

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var receiveUser: User
    private val constant = Constants()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        loadReceiverDetails()
    }

    private fun loadReceiverDetails() {
        receiveUser = intent.getParcelableExtra(constant.KEY_USER)!!
        binding.textName.text = receiveUser.name
    }

    private fun setListeners(){
        binding.imageBack.setOnClickListener{
            finish()
        }
    }
}