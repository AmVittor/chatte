package com.example.myapplication.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.database.NotesDatabase
import com.example.myapplication.databinding.FragmentCreateNoteBinding
import com.example.myapplication.models.Notes
import com.example.myapplication.utilities.NoteBottomSheetFragment
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*


class CreateNoteFragment : BaseFragment(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    private var _binding: FragmentCreateNoteBinding? = null
    private val binding get() = _binding!!

    var selectedColor = "#171C26"
    private var currentDate: String? = null
    private var READ_STORAGE_PERM = 123
    private var REQUEST_CODE_IMAGE = 456
    private var selectedImagePath = ""
    private var webLink = ""
    private var noteId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = requireArguments().getInt("noteId", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (noteId != -1) {
            launch {
                context?.let {
                    val notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                    binding.colorView.setBackgroundColor(Color.parseColor(notes.color))
                    binding.etNoteTitle.setText(notes.title)
                    binding.etNoteSubTitle.setText(notes.subTitle)
                    binding.etNoteDesc.setText(notes.noteText)
                    if (notes.imgPath != "") {
                        selectedImagePath = notes.imgPath!!
                        binding.imgNote.setImageBitmap(BitmapFactory.decodeFile(notes.imgPath))
                        binding.layoutImage.visibility = View.VISIBLE
                        binding.imgNote.visibility = View.VISIBLE
                        binding.imgDelete.visibility = View.VISIBLE
                    } else {
                        binding.layoutImage.visibility = View.GONE
                        binding.imgNote.visibility = View.GONE
                        binding.imgDelete.visibility = View.GONE
                    }

                    if (notes.webLink != "") {
                        webLink = notes.webLink!!
                        binding.tvWebLink.text = notes.webLink
                        binding.layoutWebUrl.visibility = View.VISIBLE
                        binding.etWebLink.setText(notes.webLink)
                        binding.imgUrlDelete.visibility = View.VISIBLE
                    } else {
                        binding.imgUrlDelete.visibility = View.GONE
                        binding.layoutWebUrl.visibility = View.GONE
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            BroadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
        currentDate = sdf.format(Date())
        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
        binding.tvDateTime.text = currentDate

        binding.imgDone.setOnClickListener {
            if (noteId != -1) {
                updateNote()
            } else {
                saveNote()
            }
        }

        binding.imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.imgMore.setOnClickListener {
            val noteBottomSheetFragment = NoteBottomSheetFragment.newInstance(noteId)
            noteBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                "Note Bottom Sheet Fragment"
            )
        }

        binding.imgDelete.setOnClickListener {
            binding.layoutImage.visibility = View.GONE
            binding.imgNote.visibility = View.GONE
            binding.imgDelete.visibility = View.GONE
            selectedImagePath = ""
        }


        binding.imgUrlDelete.setOnClickListener {
            binding.etWebLink.text = null
            webLink = ""
            binding.layoutWebUrl.visibility = View.GONE
        }

        binding.imgMore.setOnClickListener {
            if (EasyPermissions.hasPermissions(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                openGallery()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Esse aplicativo precisa das permições para funcionar corretamente",
                    READ_STORAGE_PERM,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }


    private fun updateNote() {
        launch {
            context?.let {
                val notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                notes.title = binding.etNoteTitle.text.toString()
                notes.subTitle = binding.etNoteSubTitle.text.toString()
                notes.noteText = binding.etNoteDesc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink
                NotesDatabase.getDatabase(it).noteDao().updateNote(notes)
                binding.etNoteTitle.setText("")
                binding.etNoteSubTitle.setText("")
                binding.etNoteDesc.setText("")
                binding.imgNote.setImageBitmap(null)
                binding.imgNote.visibility = View.GONE
                binding.imgDelete.visibility = View.GONE
                binding.tvWebLink.text = ""
                binding.layoutWebUrl.visibility = View.GONE
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun saveNote() {
        if (binding.etNoteTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Insira um título para continuar", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.etNoteSubTitle.text.isNullOrEmpty() &&
            binding.etNoteDesc.text.isNullOrEmpty() &&
            selectedImagePath.isEmpty() &&
            webLink.isEmpty()
        ) {
            Toast.makeText(context, "A nota não pode ser vazia", Toast.LENGTH_SHORT).show()
            return
        }
        launch {
            context?.let {
                val notes = Notes()
                notes.title = binding.etNoteTitle.text.toString()
                notes.subTitle = binding.etNoteSubTitle.text.toString()
                notes.noteText = binding.etNoteDesc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink
                NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)
                binding.etNoteTitle.setText("")
                binding.etNoteSubTitle.setText("")
                binding.etNoteDesc.setText("")
                binding.imgNote.setImageBitmap(null)
                binding.imgNote.visibility = View.GONE
                binding.imgDelete.visibility = View.GONE
                binding.tvWebLink.text = ""
                binding.layoutWebUrl.visibility = View.GONE
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }


    private val BroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val actionColor = intent!!.getStringExtra("action")
            when (actionColor!!) {
                "Blue" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Yellow" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Purple" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Green" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Orange" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_IMAGE) {
            if (data?.data != null) {
                binding.imgNote.visibility = View.VISIBLE
                binding.imgDelete.visibility = View.VISIBLE
                binding.layoutImage.visibility = View.VISIBLE
                binding.imgNote.setImageURI(data.data)
                selectedImagePath = getPathFromUri(data.data!!)
            }
        }
    }

    private fun getPathFromUri(contentUri: Uri): String {
        val filePath: String
        val cursor = requireContext().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path!!
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == READ_STORAGE_PERM) {
            openGallery()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    override fun onRationaleDenied(requestCode: Int) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
