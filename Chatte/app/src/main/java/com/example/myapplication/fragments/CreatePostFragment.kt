package com.example.myapplication.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.activities.ForumActivity
import com.example.myapplication.databinding.FragmentCreatePostBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CreatePostFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private var encodedImage: String = ""
    private var preferenceManager: PreferenceManager = PreferenceManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.preferenceManager(requireContext().applicationContext)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root

    }



    private fun postInFeed(){
        val constant = Constants()
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val post = hashMapOf<String, Any>()
        post[constant.KEY_DESCRIPTION] = binding.createPostDescription.text.toString()
        post[constant.KEY_IMAGE_POST] = encodedImage
        database.collection(constant.KEY_COLLECTION_POST).add(post).addOnSuccessListener { documentReference ->

            preferenceManager.putBoolean(constant.KEY_IS_SIGNED_IN, true)
            preferenceManager.putString(constant.KEY_USER_ID, documentReference.id)
            preferenceManager.putString(constant.KEY_DESCRIPTION, binding.createPostDescription.text.toString())
            preferenceManager.putString(constant.KEY_IMAGE_POST, encodedImage)
            val intent = Intent(requireContext().applicationContext, ForumActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }.addOnFailureListener {
            showToast("Erro ao publicar o post")
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
        if (result.resultCode == AppCompatActivity.RESULT_OK) {

            result.data?.data?.let { imageUri ->
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.bgImage.setImageBitmap(bitmap)
                    encodedImage = encodeImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun isValidPostInFeedDeails(): Boolean {
        if (binding.createPostDescription.text.toString().trim().isEmpty()){
            showToast("Insira uma descrição")
            return false
        }else{
            return true
        }
    }


    private fun showToast(message: String){
        Toast.makeText(requireContext().applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.publicarPost.setOnClickListener {
            if (isValidPostInFeedDeails()){
                postInFeed()
            }
            Log.d("CreatePostFragment", "publicarpostfoi")
        }

        binding.createPostButtonImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
            Log.d("CreatePostFragment", "bgl da imagem tbm pode pa ")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatePostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}