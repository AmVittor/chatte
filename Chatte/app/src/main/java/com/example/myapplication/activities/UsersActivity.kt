package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myapplication.R
import com.example.myapplication.adapters.UsersAdapter
import com.example.myapplication.databinding.ActivityUsersBinding
import com.example.myapplication.models.User
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private val constants = Constants()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_users)
        preferenceManager.preferenceManager(applicationContext)
        setListeners()
        getUsers()
    }

    private fun setListeners(){
        binding.imageBack.setOnClickListener{finish()}

    }

    private fun getUsers(){
        loading(true)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection(constants.KEY_COLLECTION_USERS).get().addOnCompleteListener { task ->
            loading(false)
            val currentUserId: String = preferenceManager.getString(constants.KEY_USER_ID)!!
            if(task.isSuccessful && task.getResult() != null){
                val users: MutableList<User> = mutableListOf()
                for (queryDocumentSnapshot: QueryDocumentSnapshot in task.getResult()){
                    if(currentUserId.equals(queryDocumentSnapshot.id)){
                        continue
                    }
                    val user = User()
                    user.name = queryDocumentSnapshot.getString(constants.KEY_NAME)!!
                    user.email = queryDocumentSnapshot.getString(constants.KEY_EMAIL)!!
                    user.image = queryDocumentSnapshot.getString(constants.KEY_IMAGE)!!
                    user.token = queryDocumentSnapshot.getString(constants.KEY_FCM_TOKEN)!!
                    users.add(user)
                }
                if(users.size > 0){
                    val usersAdapter = UsersAdapter(users)
                    binding.usersRecyclerView.setAdapter(usersAdapter)
                    binding.usersRecyclerView.visibility = View.VISIBLE

                } else {
                    showErrorMessage()
                }
            }else {
                showErrorMessage()
            }


        }
    }

    private fun showErrorMessage(){
        binding.textErrorMessage.text = String.format("%s", "Não há usuários")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}