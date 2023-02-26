package com.example.myapplication.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private val constant = Constants()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager.preferenceManager(applicationContext)
        loadUserDetails()
        getToken()
        setListeners()

    }

    private fun setListeners(){
        binding.imageSignOut.setOnClickListener{signOut()}
        binding.fabNewChat.setOnClickListener {
            startActivity(Intent(applicationContext, UsersActivity::class.java))
        }
    }

    private fun loadUserDetails(){
        binding.textName.text = preferenceManager.getString(constant.KEY_NAME)
        val bytes = Base64.decode(preferenceManager.getString(constant.KEY_IMAGE), Base64.DEFAULT)
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
    }

    private fun updateToken(token: String){
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val documentReference: DocumentReference = database.collection(constant.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(constant.KEY_USER_ID)!!
        )
        documentReference.update(constant.KEY_FCM_TOKEN, token)
            .addOnFailureListener {
                showToast("Erro ao atualizar o token")
        }
    }

    private fun signOut(){
        showToast("Encerrando a sessão")
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val documentReference: DocumentReference = database.collection(constant.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(constant.KEY_USER_ID)!!
        )
        val updates = hashMapOf<String, Any>()
        updates[constant.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference.update(updates).addOnSuccessListener{unused ->
            preferenceManager.clear()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        } .addOnFailureListener { e -> showToast("Erro ao encerrar a sessão") }
    }
}