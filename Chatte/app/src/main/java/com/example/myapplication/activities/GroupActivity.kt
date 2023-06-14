package com.example.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityGroupBinding
import com.example.myapplication.models.User
import com.example.myapplication.utilities.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupBinding
    private lateinit var groupNameEditText: EditText
    private lateinit var addUserButton: FloatingActionButton
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private val selectedUsers = mutableListOf<User>()

    companion object {
        private const val REQUEST_CODE_CREATE_GROUP = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager.preferenceManager(applicationContext)

        groupNameEditText = binding.groupNameEditText
        addUserButton = binding.addUserButton

        addUserButton.setOnClickListener {
            showUserSelection()
        }

        binding.imageBack.setOnClickListener {
            ActivityCompat.finishAfterTransition(this)
        }
    }

    private fun showUserSelection() {
        val intent = Intent(this, UsersActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_CREATE_GROUP)
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title_create_group)
        builder.setMessage(R.string.dialog_message_create_group)
        builder.setView(R.layout.dialog_create_group)
        builder.setPositiveButton(R.string.create) { _, _ ->
            val groupName = groupNameEditText.text.toString()
            val intent = Intent()
            intent.putExtra("groupName", groupName)
            intent.putExtra("selectedUsers", ArrayList(selectedUsers))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        builder.setNegativeButton(R.string.cancel, null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CREATE_GROUP && resultCode == Activity.RESULT_OK) {
            val groupName = data?.getStringExtra("groupName")
            val selectedUsers = data?.getParcelableArrayListExtra<User>("selectedUsers")

            groupNameEditText.setText(groupName)
            selectedUsers?.let {
                this.selectedUsers.clear()
                this.selectedUsers.addAll(it)
            }
        }
    }
}
