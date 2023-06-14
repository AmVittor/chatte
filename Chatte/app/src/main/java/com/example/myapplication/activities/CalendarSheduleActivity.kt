package com.example.myapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.ScheduleAdapter
import com.example.myapplication.databinding.ActivityCalendarSheduleBinding
import com.example.myapplication.models.Schedule
import com.google.android.material.bottomnavigation.BottomNavigationView

class CalendarSheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarSheduleBinding
    private lateinit var adapterSchedule: ScheduleAdapter
    private val listShedule: MutableList<Schedule> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCalendarSheduleBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_calendar
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_calendar -> true
                R.id.bottom_settings -> {
                    startActivity(Intent(applicationContext, EditUserActivity::class.java))
                    finish()
                    true
                }
                R.id.bottom_chat -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        val recyclerViewShedule = binding.recyclerView
        recyclerViewShedule.layoutManager = LinearLayoutManager(this)
        recyclerViewShedule.setHasFixedSize(true)
        adapterSchedule = ScheduleAdapter(this, listShedule)
        recyclerViewShedule.adapter = adapterSchedule
        itensDaLista()

        binding.rdAgenda.setOnClickListener {
            navegar(nomeTela = "telaAgendar")
        }


    }
    private fun itensDaLista() {
        val data = intent.getStringExtra("data")
        val hora = intent.getStringExtra("hora")
        val titulo = intent.getStringExtra("titulo")
        val tarefa = intent.getStringExtra("tarefa")
        var shedule = Schedule(data,hora,titulo,tarefa)
        var shedule1 = Schedule(data,hora,titulo,tarefa)
        listShedule.add(shedule)
        listShedule.add(shedule1)


    }
    private fun navegar(nomeTela: String){
        val intent = Intent(this, CalendarSheduleEventActivity::class.java)
        intent.putExtra("telaAgendar", nomeTela)
        startActivity(intent)
    }

}