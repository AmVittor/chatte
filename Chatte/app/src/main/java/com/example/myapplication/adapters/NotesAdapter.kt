package com.example.myapplication.adapters

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemRvNotesBinding
import com.example.myapplication.models.Notes
import kotlin.collections.ArrayList

class NotesAdapter :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    private var listener: OnItemClickListener? = null
    private var arrList = ArrayList<Notes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding =
            ItemRvNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    fun setData(arrNotesList: List<Notes>) {
        arrList = arrNotesList as ArrayList<Notes>
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNote = arrList[position]

        holder.binding.tvTitle.text = currentNote.title
        holder.binding.tvDesc.text = currentNote.noteText
        holder.binding.tvDateTime.text = currentNote.dateTime

        if (currentNote.color != null) {
            holder.binding.cardView.setCardBackgroundColor(Color.parseColor(currentNote.color))
        } else {
            holder.binding.cardView.setCardBackgroundColor(Color.parseColor(R.color.ColorLightBlack.toString()))
        }

        if (currentNote.imgPath != null) {
            holder.binding.imgNote.setImageBitmap(BitmapFactory.decodeFile(currentNote.imgPath))
            holder.binding.imgNote.visibility = View.VISIBLE
        } else {
            holder.binding.imgNote.visibility = View.GONE
        }

        if (currentNote.webLink != "") {
            holder.binding.tvWebLink.text = currentNote.webLink
            holder.binding.tvWebLink.visibility = View.VISIBLE
        } else {
            holder.binding.tvWebLink.visibility = View.GONE
        }

        holder.binding.cardView.setOnClickListener {
            listener?.onClicked(currentNote.id!!)
        }
    }

    inner class NotesViewHolder(val binding: ItemRvNotesBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onClicked(noteId: Int)
    }
}
