package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemListScheduleBinding
import com.example.myapplication.models.Schedule


class ScheduleAdapter (private val context : Context, private val listShedule: MutableList<Schedule>):
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemLista = ItemListScheduleBinding.inflate(LayoutInflater.from(context),parent, false)
        return ScheduleViewHolder(itemLista)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
//        holder.data.text = listShedule[position].data
//        holder.hora.text = listShedule[position].hora
//        holder.titulo.text = listShedule[position].titulo
//        holder.descricao.text = listShedule[position].descricao
    }

    override fun getItemCount() = listShedule.size

    inner class ScheduleViewHolder(binding: ItemListScheduleBinding): RecyclerView.ViewHolder(binding.root) {
//        val data = binding.tvDataCima
//        val hora = binding.tvHora
//        val titulo = binding.tvTituloTarefa
//        val descricao = binding.tvDescricao

    }
}