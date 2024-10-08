package com.example.project01.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.project01.R
import com.example.project01.databinding.ActivityAddPostHomeBinding
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.HomeModal

class AddPostHomeActivity : AppCompatActivity() {

    private var post: HomeModal? = null
    private lateinit var binding: ActivityAddPostHomeBinding
    private lateinit var databaseManager: FirebaseDatabaseManager
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null
    private lateinit var spinner: Spinner
    private val nameList = mutableListOf<String>()
    private val idList = mutableListOf<String>()
    private var selectedGroupId: String? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseManager = FirebaseDatabaseManager(this)

        // Retrieve the passed data
        post = intent.getParcelableExtra<HomeModal>("post")

        // Set title and description if post exists
        post?.let {
            binding.homeTitleId.setText(it.title)
            binding.homeDescId.setText(it.desc)
        }

        if (post?.title.isNullOrEmpty() && post?.desc.isNullOrEmpty()) {
            binding.hometoolbar.title = "Add Post"
        } else {
            binding.hometoolbar.title = "Edit Post"
        }

        binding.hometoolbar.setNavigationOnClickListener {
            finish()
        }

        spinner = findViewById(R.id.spinner)
        fetchData()

        binding.selectImageButton.setOnClickListener {
            openImageChooser()
        }

        binding.homeButtonId.setOnClickListener {
            val title = binding.homeTitleId.text.toString().trim()
            val description = binding.homeDescId.text.toString().trim()

            if (imageUri != null) {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    if (post == null) {
                        uploadImageAndSaveData()
                    } else {
                        // Update existing post
                        post!!.id?.let { it1 -> updateDataInFirebase(it1, title, description) }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Please fill in both title and description.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Update Home Collection
    private fun updateDataInFirebase(postId: String, title: String, description: String) {
        binding.submitLoader.visibility = View.VISIBLE
        binding.homeButtonId.visibility = View.GONE

        databaseManager.updateData(postId, title, description) { success ->
            binding.submitLoader.visibility = View.GONE
            binding.homeButtonId.visibility = View.VISIBLE
            if (success) {
                Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after successful update
            } else {
                Toast.makeText(this, "Failed to update data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save Data In home Collection
    private fun uploadImageAndSaveData() {
        binding.submitLoader.visibility = View.VISIBLE
        binding.homeButtonId.visibility = View.GONE

        val title = binding.homeTitleId.text.toString().trim()
        val desc = binding.homeDescId.text.toString().trim()

        imageUri?.let { uri ->
            databaseManager.uploadImageAndSaveData(
                uri, title, desc, selectedGroupId, isFavorite
            ) { success ->
                binding.submitLoader.visibility = View.GONE
                binding.homeButtonId.visibility = View.VISIBLE
            }
        }
    }
    private fun fetchData() {
        databaseManager.fetchGroupData { groups ->
            nameList.clear()
            idList.clear()
            groups.forEach { (name, id) ->
                nameList.add(name)
                idList.add(id)
            }

                    setupSpinner()


        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nameList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                selectedGroupId = idList[position]
                Log.d("AddPostHomeActivity", "Selected Group ID: $selectedGroupId")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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


