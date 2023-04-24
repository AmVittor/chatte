package com.example.myapplication.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.example.myapplication.adapters.RecentConversationsAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.listeners.ConversionListener
import com.example.myapplication.models.ChatMessage
import com.example.myapplication.models.User
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity(), ConversionListener {

    private lateinit var binding: ActivityMainBinding
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private val constant = Constants()
    private var conversations: MutableList<ChatMessage> = mutableListOf()
    private lateinit var conversationsAdapter: RecentConversationsAdapter
    private lateinit var database: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager.preferenceManager(applicationContext)
        init()
        loadUserDetails()
        getToken()
        setListeners()
        listenConversations()
    }

    private fun init() {
        conversations = mutableListOf()
        conversationsAdapter = RecentConversationsAdapter(conversations, this)
        binding.conversationRecyclerView.adapter = conversationsAdapter
        database = FirebaseFirestore.getInstance()
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


    private fun listenConversations(){
        database.collection(constant.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(constant.KEY_SENDER_ID, preferenceManager.getString(constant.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database.collection(constant.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(constant.KEY_RECEIVER_ID, preferenceManager.getString(constant.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }


    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            return@EventListener
        }
        if (value != null) {
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val senderId = documentChange.document.getString(constant.KEY_SENDER_ID)
                    val receiverId = documentChange.document.getString(constant.KEY_RECEIVER_ID)

                    val chatMessage = ChatMessage().apply {
                        this.senderId = senderId!!
                        this.receiverId = receiverId!!
                    }
                        if(preferenceManager.getString(constant.KEY_USER_ID).equals(senderId)){
                            chatMessage.conversionImage = documentChange.document.getString(constant.KEY_RECEIVER_IMAGE)!!
                            chatMessage.conversionName = documentChange.document.getString(constant.KEY_RECEIVER_NAME)!!
                            chatMessage.conversionId = documentChange.document.getString(constant.KEY_RECEIVER_ID)!!
                        } else {
                            chatMessage.conversionImage = documentChange.document.getString(constant.KEY_SENDER_IMAGE)!!
                            chatMessage.conversionName = documentChange.document.getString(constant.KEY_SENDER_NAME)!!
                            chatMessage.conversionId = documentChange.document.getString(constant.KEY_SENDER_ID)!!
                        }
                    chatMessage.message = documentChange.document.getString(constant.KEY_LAST_MESSAGE)!!
                    chatMessage.dateObject = documentChange.document.getDate(constant.KEY_TIMESTAMP)!!
                    conversations.add(chatMessage)
                } else if(documentChange.type == DocumentChange.Type.MODIFIED){
                    for (i in 0 until conversations.size) {
                        val senderId = documentChange.document.getString(constant.KEY_SENDER_ID)
                        val receiverId = documentChange.document.getString(constant.KEY_RECEIVER_ID)

                        if (conversations[i].senderId == senderId && conversations[i].receiverId == receiverId) {
                            conversations[i].message = documentChange.document.getString(constant.KEY_LAST_MESSAGE)!!
                            conversations[i].dateObject = documentChange.document.getDate(constant.KEY_TIMESTAMP)!!
                            break
                        }
                    }
                }
            }
            conversations.sortWith(compareByDescending { it.dateObject })
            conversationsAdapter.notifyDataSetChanged()
            binding.conversationRecyclerView.smoothScrollToPosition(0)
            binding.conversationRecyclerView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getUserInfo(userId: String, onSuccess: (name: String, image: String) -> Unit) {
        database.collection(constant.KEY_COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString(constant.KEY_NAME)!!
                    val image = document.getString(constant.KEY_IMAGE)!!
                    onSuccess(name, image)
                }
            }
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
        documentReference.update(updates).addOnSuccessListener{
            preferenceManager.clear()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        } .addOnFailureListener { showToast("Erro ao encerrar a sessão") }
    }

    override fun onConversionClicked(user: User){
        val intent = Intent(applicationContext,  ChatActivity::class.java)
        intent.putExtra(constant.KEY_USER, user)
        startActivity(intent)
    }
}