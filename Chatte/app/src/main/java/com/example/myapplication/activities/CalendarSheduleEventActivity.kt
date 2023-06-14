package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.example.myapplication.databinding.ActivityCalendarSheduleEventBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class CalendarSheduleEventActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCalendarSheduleEventBinding
    private val calendar: Calendar = Calendar.getInstance()
    private var data: String = ""
    private var hora: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarSheduleEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val nome = intent.extras?.getString("nome").toString()

        val datePicker = binding.datePicker
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            var dia = dayOfMonth.toString()
            val mes: String

            if (dayOfMonth < 10){
                dia = "0$dayOfMonth"
            }
            if (monthOfYear < 10){
                mes = "" + (monthOfYear+1)
            }else{
                mes = (monthOfYear +1).toString()
            }

            data = "$dia / $mes / $year"

            binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
                val minuto : String

                if(minute < 10){
                    minuto = "0$minute"
                }else{
                    minuto = minute.toString()
                }

                hora = "$hourOfDay:$minuto"
            }


            binding.timePicker.setIs24HourView(true)

            binding.btAgendar.setOnClickListener {

                val tarefa = binding.etTarefa.text.toString()
                val titulo = binding.etTitulo.text.toString()

                when{
                    hora.isEmpty() -> {
                        mensagem(it, "Preencha a hora!", "#FF0000")
                    }
                    data.isEmpty() -> {
                        mensagem(it, "Preencha a data!", "#FF0000")
                    }
                    titulo.isEmpty() -> {
                        mensagem(it, "Preencha o título!", "#FF0000")
                    }
                    tarefa.isEmpty() -> {
                        mensagem(it, "Preencha a tarefa!", "#FF0000")
                    }
                    tarefa.isNotEmpty() && data.isNotEmpty() && hora.isNotEmpty() && titulo.isNotEmpty() ->{
                        mensagem(it, "Tarefa Agendada com sucesso.", "#FF03DAC5")
                        mandarDados()
                    }
                    else -> {
                        mensagem(it, "Agenda uma tarefa!", "#FF0000")
                    }
                }


            }

            binding.vtAgenda.setOnClickListener {
                navegar(nomeTela = "telaAgenda")
            }

        }
    }
    @SuppressLint("ShowToast")
    private fun mensagem (view: View, mensagem: String, cor: String) {
        val snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(cor))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun navegar(nomeTela: String){
        val intent = Intent(this, CalendarSheduleActivity::class.java)
        intent.putExtra("telaAgenda", nomeTela)
        startActivity(intent)
    }

    private fun mandarDados(){
        val intentDados = Intent(this, CalendarSheduleActivity::class.java)
        val titulo = binding.etTitulo.text.toString()
        val tarefa = binding.etTarefa.text.toString()
        intentDados.putExtra("data", data)
        intentDados.putExtra("hora", hora)
        intentDados.putExtra("titulo", titulo)
        intentDados.putExtra("tarefa", tarefa)

    }

}