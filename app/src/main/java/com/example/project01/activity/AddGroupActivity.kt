package com.example.project01.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.project01.databinding.ActivityAddGroupBinding
import com.example.project01.firebaseold.GroupFirebaseManager
import com.example.project01.modal.GroupModal
import com.example.project01.viewmodal.AddGroupViewModel

class AddGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding
    private lateinit var databaseManager: GroupFirebaseManager
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null
    private var group: GroupModal? = null
    private lateinit var addGroupViewModel: AddGroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseManager = GroupFirebaseManager(this)
        addGroupViewModel= ViewModelProvider(this).get(AddGroupViewModel::class)

        // Observe the LiveData to get success/failure status
        addGroupViewModel.isGroupSaved.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Groups Saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }
        })


        group = intent.getParcelableExtra("group")

        //Set Update Data
        group?.let {
            binding.nameId.setText(it.name)
            binding.imageView.visibility = View.VISIBLE
            it.imageUrl?.let { urlString ->
                Glide.with(this).load(urlString).into(binding.imageView)
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

            if (imageUri != null) {
                if (name.isNotEmpty()) {
                    if (group == null) {
                        saveData()
                    } else {
                        group!!.id?.let { groupId ->
                            updateData(groupId)
                        } ?: run {
                            Toast.makeText(this, "Group ID is null.", Toast.LENGTH_SHORT).show()
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

        imageUri?.let { uri ->
            addGroupViewModel.saveGroup(uri,name)
        }
    }

    //Update Group Collection
    private fun updateData(groupId: String) {
        binding.submitLoader.visibility = View.VISIBLE
        binding.buttonId.visibility = View.GONE
        val name = binding.nameId.text.toString().trim()
        imageUri?.let { uri ->
           addGroupViewModel.updateGroup(groupId,name,uri)


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
