package com.example.myapplication.fragments


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.PostAdapter
import com.example.myapplication.databinding.FragmentCreatePostBinding
import com.example.myapplication.databinding.FragmentForumBinding
import com.example.myapplication.models.Post
import com.example.myapplication.network.ApiClient
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ForumFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForumFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var database: FirebaseFirestore
    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!
    private val constant = Constants()
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private var apiClient: ApiClient = ApiClient()
    private lateinit var recyclerView: RecyclerView
    private lateinit var postsList: List<Post>
    private var db = Firebase.firestore
    private lateinit var postAdapter: PostAdapter
    private var encodedImage: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forum, container, false)

        val btnPost: ImageView = view.findViewById(R.id.post_forum)



        recyclerView = view.findViewById(R.id.recycle_view_forum)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        FirebaseFirestore.getInstance().collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                postsList = ArrayList()
                for (documents in documents){
                    val post = documents.toObject(Post::class.java)
                    (postsList as ArrayList<Post>).add(post!!)

                }
                recyclerView.adapter = PostAdapter(posts = postsList, this)
            }
            .addOnFailureListener {
                showToast("An error Occurred: ${it.localizedMessage}")
            }

//        db.collection("posts").get().addOnSuccessListener {
//                if (!it.isEmpty){
//                    for (data in it.documents){
//                        val post: Post? = data.toObject<Post>(Post::class.java)
//                        postsList.add(post!!)
//                    }
//                    recyclerView.adapter = PostAdapter(posts = postsList, this, )
//                }
//        } .addOnFailureListener {
//            showToast("Erro no Adapter")
//        }



        btnPost.setOnClickListener {
            val postFragment = CreatePostFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, postFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        return view
    }

    private fun fetchData() {

    }


    private fun getBitmapFromEncodedString(encodedImage: String): Bitmap {
        val bytes = Base64.decode(preferenceManager.getString(constant.KEY_IMAGE), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ForumFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}

