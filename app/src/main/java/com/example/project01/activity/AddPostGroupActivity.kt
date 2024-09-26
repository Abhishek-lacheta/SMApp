package com.example.project01.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.project01.databinding.ActivityAddPostGroupBinding
import com.example.project01.firebase.FirebaseDatabaseManager

class AddPostGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostGroupBinding
    private lateinit var databaseManager: FirebaseDatabaseManager
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseManager = FirebaseDatabaseManager(this)

        binding.Grouptoolbar.setTitle("Add Post")
        binding.Grouptoolbar.setNavigationOnClickListener {
            finish()
        }
        binding.selectImageButton.setOnClickListener {
            openImageChooser()
        }
        binding.buttonId.setOnClickListener {
            if (imageUri != null) {
                val name = binding.nameId.text.toString().trim()
                databaseManager.uploadImageAndSaveData(imageUri!!, name) { success ->
                    if (success) {
                        // Handle success if needed
                    }
                }
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.imageView.setImageURI(imageUri)
            binding.imageView.visibility = View.VISIBLE
        }
    }
}
