package com.example.myapplication.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemContainerUserBinding
import com.example.myapplication.models.User

class UsersAdapter: RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private val users: List<User>

    constructor(users: List<User>) {
        this.users = users
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemContainerUserBinding: ItemContainerUserBinding = ItemContainerUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(itemContainerUserBinding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UserViewHolder(private val binding: ItemContainerUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setUserData(user: User) {
            binding.textName.text = user.name
            binding.textEmail.text = user.email
           // binding.imageProfile.setImageBitmap(getUserImage(user.image))
        }
    }


    private fun getuserImage(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}