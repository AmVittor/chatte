package com.example.myapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.widget.Button
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_settings
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_settings -> true
                R.id.bottom_calendar -> {
                    startActivity(Intent(applicationContext, NoteMain::class.java))
                    finish()
                    true
                }
                R.id.bottom_chat-> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }



    }



    private fun navegar(nomeTela: String){
        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("telaEditar", nomeTela)
        startActivity(intent)
    }


}