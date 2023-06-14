package com.example.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityForumBinding
import com.example.myapplication.fragments.ForumFragment

@Suppress("DEPRECATION")
class ForumActivity : AppCompatActivity() {

    internal var selectedFragment: Fragment? = null
    internal var selectedActivity: Activity? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.bottom_forum -> {
                selectedFragment = ForumFragment()
            }
            R.id.bottom_chat -> {
                selectedActivity = MainActivity()
                item.isChecked = false
                startActivity(Intent(this@ForumActivity, MainActivity::class.java))
            }
            R.id.bottom_squads -> {
                item.isChecked = false
            }
            R.id.bottom_calendar -> {
                item.isChecked = false
                startActivity(Intent(this@ForumActivity, NoteMain::class.java))
            }
            R.id.bottom_settings -> {
                item.isChecked = false
                startActivity(Intent(this@ForumActivity, ProfileActivity::class.java))
            }
        }

        if(selectedFragment != null){
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                selectedFragment!!
            ).commit()
        }



        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)



        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            ForumFragment()
        ).commit()



    }




}