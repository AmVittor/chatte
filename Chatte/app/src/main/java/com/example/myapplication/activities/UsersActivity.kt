package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.myapplication.adapters.UsersAdapter
import com.example.myapplication.databinding.ActivityUsersBinding
import com.example.myapplication.listeners.UserListener
import com.example.myapplication.models.User
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class UsersActivity : BaseActivity(), UserListener {

    private lateinit var binding: ActivityUsersBinding
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private val constants = Constants()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager.preferenceManager(applicationContext)
        setListeners()
        getUsers()
    }

    private fun setListeners(){
        binding.imageBack.setOnClickListener{
            ActivityCompat.finishAfterTransition(this)
        }
    }

    private fun getUsers(){
        loading(true)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection(constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task ->
                loading(false)
                val currentuserId: String = preferenceManager.getString(constants.KEY_USER_ID)!!
                if(task.isSuccessful && task.result != null){
                    val users: MutableList<User> = mutableListOf()
                    for (queryDocumentSnapshot: QueryDocumentSnapshot in task.result){
                        if(currentuserId == queryDocumentSnapshot.id){
                            continue
                        }
                        val user = User()
                        user.name = queryDocumentSnapshot.getString(constants.KEY_NAME)!!
                        user.email = queryDocumentSnapshot.getString(constants.KEY_EMAIL)!!
                        user.image = queryDocumentSnapshot.getString(constants.KEY_IMAGE)!!
                        // Sim, so o token precisa do .toString, se tirar quebra, pq? Não sei
                        user.token = queryDocumentSnapshot.getString(constants.KEY_FCM_TOKEN).toString()
                        user.id = queryDocumentSnapshot.id
                        users.add(user)
                    }
                    if (users.isNotEmpty()){
                        val usersAdapter = UsersAdapter(users, this)
                        binding.usersRecyclerView.adapter = usersAdapter
                        binding.usersRecyclerView.visibility = View.VISIBLE
                    } else{
                        showErrorMessage()
                    }
                } else {
                    showErrorMessage()
                }
            }
    }

    private fun showErrorMessage(){
        binding.textErrorMessage.text = String.format("%s", "No user available")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onUserClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(constants.KEY_USER, user)
        startActivity(intent)
        finish()
    }

    override fun initiateVideoMeeting(user: User) {
        TODO("Not yet implemented")
    }

    override fun initiateAudioMetting(user: User) {
        TODO("Not yet implemented")
    }

}


