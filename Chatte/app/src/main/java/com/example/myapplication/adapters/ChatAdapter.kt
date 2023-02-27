package com.example.myapplication.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemContainerReceivedMessageBinding
import com.example.myapplication.databinding.ItemContainerSentMessageBinding
import com.example.myapplication.models.ChatMessage

class ChatAdapter(
    var chatMessage: List<ChatMessage>,
    private var receiverProfileImage: Bitmap,
    private var senderId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == VIEW_TYPE_SENT){
            return SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
            )
        } else{
            return ReceiveMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if(getItemViewType(position) == VIEW_TYPE_SENT){
           (holder as SentMessageViewHolder).setData(chatMessage[position])
       } else {
           (holder as ReceiveMessageViewHolder).setData(chatMessage[position], receiverProfileImage)
       }
    }

    override fun getItemCount(): Int {
       return chatMessage.size
    }

    override fun getItemViewType(position: Int): Int {
        if(chatMessage[position].senderId == senderId){
            return VIEW_TYPE_SENT
        } else {
            return VIEW_TYPE_RECEIVED
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemContainerSentMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(chatMessage: ChatMessage) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
        }
    }

    class ReceiveMessageViewHolder(private val binding: ItemContainerReceivedMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(chatMessage: ChatMessage, receiverProfileImage: Bitmap) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
            binding.imageProfile.setImageBitmap(receiverProfileImage)
        }
    }


}