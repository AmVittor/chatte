package com.example.myapplication.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.myapplication.R
import com.example.myapplication.adapters.ChatAdapter
import com.example.myapplication.databinding.ActivityChatBinding
import com.example.myapplication.models.ChatMessage
import com.example.myapplication.models.User
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.ApiService
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

import kotlin.collections.HashMap

class ChatActivity : BaseActivity() {

    private var username = ""
    private var friendsUsername = ""
    private var isPeerConnected = false
    private var firebaseRef = Firebase.database.getReference("users")
    private var isAudio = true
    private var isVideo = true
    private var apiClient: ApiClient = ApiClient()
    private lateinit var binding: ActivityChatBinding
    private var receiveUser: User = User()
    private val constant = Constants()
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private lateinit var database: FirebaseFirestore
    private var conversionId: String? = null
    private var isReceiverAvailable: Boolean = false
    private val permissions = arrayOf(Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val requestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        loadReceiverDetails()
        init()
        listenMessages()
        loadUserDetails()

        if (!isPermissionGranted()) {
            askPermissions()
        }

        username = preferenceManager.getString(constant.KEY_NAME)!!
        binding.callBtn.setOnClickListener {
            friendsUsername = receiveUser.name
            sendCallRequest()
        }

        binding.toggleAudioBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            binding.toggleAudioBtn.setImageResource(if (isAudio) R.drawable.ic_baseline_mic_24 else R.drawable.ic_baseline_mic_off_24 )
        }

        binding.toggleVideoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            binding.toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24 )
        }

        setupWebView()
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun isPermissionGranted(): Boolean {
        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    private fun init(){
        apiClient = ApiClient()
        preferenceManager.preferenceManager(applicationContext)
        // chatMessage = ArrayListOf<>()
        chatAdapter = ChatAdapter(
            chatMessages,
            getBitmapFromEncodedString(receiveUser.image),
            preferenceManager.getString(constant.KEY_USER_ID)!!
        )
        binding.chatRecyclerView.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()
    }


    private fun sendMessage(){
        val message = hashMapOf<String, Any>()
        message[constant.KEY_SENDER_ID] = preferenceManager.getString(constant.KEY_USER_ID)!!
        message[constant.KEY_RECEIVER_ID] = receiveUser.id
        message[constant.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[constant.KEY_TIMESTAMP] = Date()
        database.collection(constant.KEY_COLLECTION_CHAT).add(message)
        if(conversionId != null){
            updateConversion(binding.inputMessage.text.toString())
        } else {
            val conversion = HashMap<String, Any>()
            conversion[constant.KEY_SENDER_ID] = preferenceManager.getString(constant.KEY_USER_ID)!!
            conversion[constant.KEY_SENDER_NAME] = preferenceManager.getString(constant.KEY_NAME)!!
            conversion[constant.KEY_RECEIVER_ID] = receiveUser.id
            conversion[constant.KEY_RECEIVER_NAME] = receiveUser.name
            conversion[constant.KEY_RECEIVER_IMAGE] = receiveUser.image
            conversion[constant.KEY_LAST_MESSAGE] = binding.inputMessage.text.toString()
            conversion[constant.KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }
        if(!isReceiverAvailable){
            try{
                val tokens = JSONArray()
                tokens.put(receiveUser.token)

                val data = JSONObject()
                data.put(constant.KEY_USER_ID, preferenceManager.getString(constant.KEY_USER_ID))
                data.put(constant.KEY_NAME, preferenceManager.getString(constant.KEY_NAME))
                data.put(constant.KEY_FCM_TOKEN, preferenceManager.getString(constant.KEY_FCM_TOKEN))
                data.put(constant.KEY_MESSAGE, binding.inputMessage.text.toString())


                val body = JSONObject()
                body.put(constant.REMOTE_MSG_DATA, data)
                body.put(constant.REMOTE_MSG_REGISTRATION_IDS, tokens)

                sendNotification(body.toString())
            } catch (exception: Exception){
                showToast(exception.message!!)
            }
        }
        binding.inputMessage.text = null
    }
    private fun loadUserDetails(){
        binding.textName2.text = preferenceManager.getString(constant.KEY_NAME)
        }
    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun sendNotification(messageBody: String) {

        apiClient.getClient().create(ApiService::class.java).sendMessage(constant.getRemoteMsgHeaders2()!!, messageBody
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    try{
                        if (response.body() != null){
                            val responseJson = JSONObject(response.body())
                            val results: JSONArray = responseJson.getJSONArray("results")
                            if(responseJson.getInt("failure") == 1){
                                val error: JSONObject = results.get(0) as JSONObject
                                showToast(error.getString("error"))
                                return
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    showToast("Notificação enviada com sucesso")
                } else {
                    showToast("Error: " + response.code() )
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                showToast(t.message!!)
            }
        })
    }



    private fun listenAvailabilityOfReceiver() {
        database.collection(constant.KEY_COLLECTION_USERS).document(receiveUser.id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    val availability = value.getLong(constant.KEY_AVAILABILITY)?.toInt()
                    isReceiverAvailable = availability == 1
                    val token = value.getString(constant.KEY_FCM_TOKEN)
                    if (token != null) {
                        receiveUser.token = token
                    }
                }
                if (isReceiverAvailable) {
                    binding.textAvailability.visibility = View.VISIBLE
                } else {
                    binding.textAvailability.visibility = View.GONE
                }
            }
    }

    private fun listenMessages(){
        database.collection(constant.KEY_COLLECTION_CHAT)
            .whereEqualTo(constant.KEY_SENDER_ID, preferenceManager.getString(constant.KEY_USER_ID))
            .whereEqualTo(constant.KEY_RECEIVER_ID, receiveUser.id)
            .addSnapshotListener(eventListener)
        database.collection(constant.KEY_COLLECTION_CHAT)
            .whereEqualTo(constant.KEY_SENDER_ID, receiveUser.id)
            .whereEqualTo(constant.KEY_RECEIVER_ID, preferenceManager.getString(constant.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private fun getBitmapFromEncodedString(encodedImage: String): Bitmap {
        val bytes = Base64.decode(preferenceManager.getString(constant.KEY_IMAGE), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun loadReceiverDetails() {
        receiveUser = intent.getParcelableExtra(constant.KEY_USER)!!
        binding.textName.text = receiveUser.name
        Log.d("Nome", receiveUser.name)
    }
    private fun setListeners(){
        binding.imageBack.setOnClickListener{ finish() }
        binding.layoutSend.setOnClickListener { sendMessage() }
    }

    private fun getReadableDatetime(date: Date): String{
        return SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault()).format(date)

    }

    private fun addConversion(conversion: HashMap<String, Any>) {
        database.collection(constant.KEY_COLLECTION_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener { documentReference ->
                conversionId = documentReference.id
            }
    }

    private fun updateConversion(message: String) {
        val documentReference: DocumentReference =
            database.collection(constant.KEY_COLLECTION_CONVERSATIONS).document(conversionId!!)
        documentReference.update(
            constant.KEY_LAST_MESSAGE, message,
            constant.KEY_TIMESTAMP, Date()
        )
    }


    private fun checkForConversion(){
        if(chatMessages.size != 0){
            checkForConversionRemately(
                preferenceManager.getString(constant.KEY_USER_ID)!!,
                receiveUser.id
            )
            checkForConversionRemately(
                receiveUser.id,
                preferenceManager.getString(constant.KEY_USER_ID)!!
            )
        }
    }

    private fun checkForConversionRemately(senderid: String , receiverId: String){
        database.collection(constant.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(constant.KEY_SENDER_ID, senderid)
            .whereEqualTo(constant.KEY_RECEIVER_ID,receiverId )
            .get()
            .addOnCompleteListener(conversionOnCompleteListener)
    }

    private val conversionOnCompleteListener = OnCompleteListener<QuerySnapshot> { task ->
        if (task.isSuccessful && task.result != null && task.result.documents.isNotEmpty()) {
            val documentSnapshot = task.result.documents[0]
            conversionId = documentSnapshot.id
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private val eventListener = com.google.firebase.firestore.EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            return@EventListener
        }
        value?.let { _ ->
            val count = chatMessages.size
            for (documentChange in value.documentChanges) {
                val chatMessage = ChatMessage()
                chatMessage.senderId = documentChange.document.getString(constant.KEY_SENDER_ID).toString()
                chatMessage.receiverId = documentChange.document.getString(constant.KEY_RECEIVER_ID).toString()
                chatMessage.message = documentChange.document.getString(constant.KEY_MESSAGE).toString()
                chatMessage.dateTime = getReadableDatetime(documentChange.document.getDate(constant.KEY_TIMESTAMP)!!)
                chatMessage.dateObject = documentChange.document.getDate(constant.KEY_TIMESTAMP)!!
                chatMessages.add(chatMessage)
            }
            chatMessages.sortWith(compareBy { it.dateObject })
            if (count == 0) {
                chatAdapter.notifyDataSetChanged()
            } else {
                chatAdapter.notifyItemRangeChanged(chatMessages.size, chatMessages.size)
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }
            binding.chatRecyclerView.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
        if(conversionId == null){
            checkForConversion()
        }
    }

    override fun onResume() {
        super.onResume()
        listenAvailabilityOfReceiver()
    }

    private fun sendCallRequest() {
        Log.d("Message", "Chegou no request")
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show()
            return
        }

        firebaseRef.child(friendsUsername).child("incoming").setValue(username)
        firebaseRef.child(friendsUsername).child("isAvailable").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.value.toString() == "true") {
                    listenForConnId()
                }

            }

        })

    }

    private fun listenForConnId() {
        firebaseRef.child(friendsUsername).child("connId").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null)
                    return
                switchToControls()
                callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
            }

        })
    }

    private fun setupWebView() {

        binding.webView.webChromeClient = object: WebChromeClient() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding.webView.addJavascriptInterface(JavascriptInterface(this), "Android")

        loadVideoCall()
    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        binding.webView.loadUrl(filePath)

        binding.webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    var uniqueId = ""

    private fun initializePeer() {

        uniqueId = getUniqueID()

        callJavascriptFunction("javascript:init(\"${uniqueId}\")")
        firebaseRef.child(username).child("incoming").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                onCallRequest(snapshot.value as? String)
            }

        })

    }

    @SuppressLint("SetTextI18n")
    private fun onCallRequest(caller: String?) {
        if (caller == null) return

        binding.callLayout.visibility = View.VISIBLE
        binding.incomingCallTxt.text = "$caller is calling..."

        binding.acceptBtn.setOnClickListener {
            firebaseRef.child(username).child("connId").setValue(uniqueId)
            firebaseRef.child(username).child("isAvailable").setValue(true)

            binding.callLayout.visibility = View.GONE
            switchToControls()
        }

        binding.rejectBtn.setOnClickListener {
            firebaseRef.child(username).child("incoming").setValue(null)
            binding.callLayout.visibility = View.GONE
        }

    }

    fun turnOff(){
        binding.toggleTurnOff.setOnClickListener {
            isPeerConnected = false
        }
    }

    private fun switchToControls() {
        binding.callBtn.visibility = View.GONE
        binding.sendFileBtn.visibility = View.GONE
        binding.imageVideoMeeting.visibility = View.GONE
        binding.textAvailability.visibility = View.GONE
        binding.viewBackground.visibility = View.GONE
        binding.chatRecyclerView.visibility = View.GONE
        binding.layoutSend.visibility = View.GONE
        binding.inputMessage.visibility = View.GONE
        binding.headerBackground.visibility = View.GONE
        binding.callControlLayout.visibility = View.VISIBLE
    }


    private fun getUniqueID(): String {
        return UUID.randomUUID().toString()
    }

    private fun callJavascriptFunction(functionString: String) {
        binding.webView.post { binding.webView.evaluateJavascript(functionString, null) }
    }


    fun onPeerConnected() {
        isPeerConnected = true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        firebaseRef.child(username).setValue(null)
        binding.webView.loadUrl("about:blank")
        super.onDestroy()
    }

    fun selecionarArquivo(view: View) {}
}