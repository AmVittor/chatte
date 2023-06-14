package com.example.myapplication.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.SpannableStringBuilder
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemPostBinding
import com.example.myapplication.fragments.ForumFragment
import com.example.myapplication.models.Post
//private val receiverProfileImage: Bitmap,
class PostAdapter(private val posts: List<Post>, private val context: ForumFragment, ) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = posts[position]
        holder.description.text = post.description

        Glide.with(context)
            .asBitmap()
            .load(getPostImage(post.imagePost))
            .into(holder.imagePost)


//        Glide.with(context).load(posts[position].image).into(holder.binding.imagePost)
//        holder.setPostData(posts[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val description: TextView = itemView.findViewById(R.id.description)
        val imagePost: ImageView = itemView.findViewById(R.id.image_post)





//        fun setPostData(post: Post) {
//            binding.description.text = post.description
//            binding.imagePost.setImageBitmap(getPostImage(post.image))
////            binding.imageProfilePost.setImageBitmap(receiverProfileImage)
//        }

    }



    private fun getPostImage(encodedImage: String): Bitmap? {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }



}