package com.example.project01.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.project01.databinding.ActivityAddPostGroupBinding
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.GroupModal

class AddPostGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostGroupBinding
    private lateinit var databaseManager: FirebaseDatabaseManager
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null
    private var group: GroupModal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseManager = FirebaseDatabaseManager(this)
        group = intent.getParcelableExtra("group")

        group?.let {
            binding.nameId.setText(it.name)
            // Load image with Glide if it's a valid URL
            it.imageUrl?.let { urlString ->
                Glide.with(this)
                    .load(urlString) // Load the image URL
                    .into(binding.imageView) // Set the image to ImageView
            }
        }

        if (group?.name.isNullOrEmpty()) {
            binding.Grouptoolbar.setTitle("Add Group")
        } else {
            binding.Grouptoolbar.setTitle("Edit Group")
        }
        binding.Grouptoolbar.setNavigationOnClickListener {
            finish()
        }
        binding.selectImageButton.setOnClickListener {
            openImageChooser()
        }
        binding.buttonId.setOnClickListener {
            val name = binding.nameId.text.toString().trim()
            val image = binding.imageView.setImageURI(imageUri)

            if (imageUri != null) {
                if (name.isNotEmpty()) {
                    if (group == null) {
                        saveData()
                    } else {
                        group!!.id?.let { it2 ->
                            updateGroupInFirebase(
                                it2,
                                name,
                                image.toString()
                            )
                        }
                    }
                } else {
                    Toast.makeText(this, "Please enter a name.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save data in Group Collection
    private fun saveData() {
        binding.submitLoader.visibility = View.VISIBLE
        binding.buttonId.visibility = View.GONE

        val name = binding.nameId.text.toString().trim()
        val image = binding.imageView.setImageURI(imageUri)

        imageUri?.let { uri ->
            databaseManager.uploadImageAndSaveData(uri, name) { success ->
                binding.submitLoader.visibility = View.GONE
                binding.buttonId.visibility = View.VISIBLE
            }
        }
    }

    //Update Group Collection
    private fun updateGroupInFirebase(
        groupId: String,
        name: String,
        imageUrl: String
    ) {
        binding.submitLoader.visibility = View.VISIBLE
        binding.buttonId.visibility = View.GONE

        databaseManager.updateGroupData(groupId, name, imageUrl) { success ->
            binding.submitLoader.visibility = View.GONE
            binding.buttonId.visibility = View.VISIBLE
            if (success) {
                Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after successful update
            } else {
                Toast.makeText(this, "Failed to update data", Toast.LENGTH_SHORT).show()
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
