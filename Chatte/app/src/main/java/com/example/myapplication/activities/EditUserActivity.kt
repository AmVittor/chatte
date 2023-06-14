package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import com.example.myapplication.databinding.ActivityEditUserBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream


class EditUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditUserBinding
    private var preferenceManager: PreferenceManager = PreferenceManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager.preferenceManager(applicationContext)

        binding.btnAlterar.setOnClickListener {
            editarPerfil()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun editarPerfil() {
        val constant = Constants()
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userId = preferenceManager.getString(constant.KEY_USER_ID)

        if (userId != null) {
            val userRef = database.collection(constant.KEY_COLLECTION_USERS).document(userId)
            val updates = hashMapOf<String, Any>(
                constant.KEY_NAME to binding.editarNome.text.toString(),
                constant.KEY_EMAIL to binding.editarEmail.text.toString(),
            )

            userRef.update(updates)
                .addOnSuccessListener {
                    showToast("Perfil atualizado com sucesso")
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener { exception ->
                    showToast("Erro ao atualizar o perfil: ${exception.message}")
                }
        } else {
            showToast("ID do usuário não encontrado")
        }
    }
}
