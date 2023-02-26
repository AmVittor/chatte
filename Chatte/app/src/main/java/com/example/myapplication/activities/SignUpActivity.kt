package com.example.myapplication.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var encodedImage: String = ""
    private var preferenceManager: PreferenceManager = PreferenceManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager.preferenceManager(applicationContext)
        setListener()
    }

    private fun setListener(){
        binding.textSignIn.setOnClickListener{
            finish()
        }
        binding.buttonSignUp.setOnClickListener{
            if (isValidSignUpDeails()){
                signUp()
            }
        }
        binding.layoutImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }

    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun signUp(){
        loading(true)
        val constant = Constants()
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user = hashMapOf<String, Any>()
        user[constant.KEY_NAME] = binding.inputName.text.toString()
        user[constant.KEY_EMAIL] = binding.inputEmail.text.toString()
        user[constant.KEY_PASSWORD] = binding.inputPassword.text.toString()
        user[constant.KEY_IMAGE] = encodedImage
        database.collection(constant.KEY_COLLECTION_USERS).add(user).addOnSuccessListener { documentReference ->
          loading(false)
            preferenceManager.putBoolean(constant.KEY_IS_SIGNED_IN, true)
            preferenceManager.putString(constant.KEY_USER_ID, documentReference.id)
            preferenceManager.putString(constant.KEY_NAME, binding.inputName.text.toString())
            preferenceManager.putString(constant.KEY_IMAGE, encodedImage)
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }.addOnFailureListener {
            loading(false)
            showToast("Erro ao realisar o login")
        }
    }

    private fun encodeImage(bitmap: Bitmap): String{
        val previewWidth = 150
        val previewheight: Int = bitmap.height * previewWidth / bitmap.width
        val previewBitmap: Bitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewheight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { imageUri ->
                try {
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.visibility= View.GONE
                    encodedImage = encodeImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isValidSignUpDeails(): Boolean {
        if (binding.inputName.text.toString().trim().isEmpty()) {
            showToast("Insira o nome")
            return false
        } else if (binding.inputEmail.text.toString().trim().isEmpty()) {
            showToast("Insira um e-mail")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            showToast("Insira um e-mail válido")
            return false

        } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
            showToast("Insira uma senha")
            return false
        } else if (binding.inputConfirmPassword.text.toString().trim().isEmpty()) {
            showToast("Confirme a senha")
            return false
        } else if (binding.inputPassword.text.toString() != binding.inputConfirmPassword.text.toString()
        ) {
            showToast("As senhas devem ser iguais")
            return false
        } else {
            return true
        }
    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE

        } else{
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignUp.visibility = View.VISIBLE

        }
    }
}

