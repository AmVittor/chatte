package com.example.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

open class BaseActivity: AppCompatActivity() {

    private lateinit var documentReference: DocumentReference
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private val constant = Constants()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.preferenceManager(applicationContext)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        documentReference = database.collection(constant.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(constant.KEY_USER_ID)!!)
    }

    override fun onPause() {
        super.onPause()
        documentReference.update(constant.KEY_AVAILABILITY, 0)
    }

    override fun onResume() {
        super.onResume()
        documentReference.update(constant.KEY_AVAILABILITY, 1)
    }
}