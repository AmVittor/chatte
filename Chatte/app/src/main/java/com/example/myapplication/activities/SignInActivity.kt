package com.example.myapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.myapplication.databinding.ActivitySignInBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private val constant = Constants()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.preferenceManager(applicationContext)
        // MANTER O LOGIN
        if(preferenceManager.getBoolean(constant.KEY_IS_SIGNED_IN)){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }


    private fun setListeners() {
        binding.textCreateAccount.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java)) }
        binding.buttonSignIn.setOnClickListener{
            if (isValidSignInDetails()){
                signIn()
            }
        }
    }

    private fun signIn(){
        loading(true)

        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection(constant.KEY_COLLECTION_USERS)
            .whereEqualTo(constant.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(constant.KEY_PASSWORD, binding.inputPassword.text.toString())
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful && task.result != null && task.result.documents.size > 0){
                    val documentSnapshot: DocumentSnapshot = task.result.documents[0]
                    preferenceManager.putBoolean(constant.KEY_IS_SIGNED_IN, true)
                    preferenceManager.putString(constant.KEY_USER_ID, documentSnapshot.id)
                    preferenceManager.putString(constant.KEY_NAME, documentSnapshot.getString(constant.KEY_NAME)!!)
                    preferenceManager.putString(constant.KEY_IMAGE, documentSnapshot.getString(constant.KEY_IMAGE)!!)
                    preferenceManager.putString(constant.KEY_IMAGE, documentSnapshot.getString(constant.KEY_IMAGE)!!)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    loading(false)
                    showToast("Erro ao realisar o login")
                }
            }
    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.buttonSignIn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else{
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignIn.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignInDetails(): Boolean{
        if(binding.inputEmail.text.toString().trim().isEmpty()){
            showToast("Insira o email")
            return false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()){
            showToast("Insira um email válido")
            return false
        } else if(binding.inputPassword.text.toString().trim().isEmpty()){
            showToast("Insira sua senha")
            return false
        } else{
            return true
        }
    }
}