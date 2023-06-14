package com.example.myapplication.listeners

import com.example.myapplication.models.Post

interface PostListener {

    fun onPostClicked(post: Post)

}