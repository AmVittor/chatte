package com.example.myapplication.listeners

import com.example.myapplication.models.User

interface ConversionListener {
    fun onConversionClicked(user: User)
}