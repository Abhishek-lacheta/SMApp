package com.example.project01.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.project01.R
import com.example.project01.databinding.ActivityAddPostBinding
import com.example.project01.modal.HomeModal
import com.example.project01.firebaseold.GroupFirebaseManager
import com.example.project01.viewmodal.AddPostViewModel

class AddPostActivity : AppCompatActivity() {

    private var post: HomeModal? = null
    private lateinit var binding: ActivityAddPostBinding
    private lateinit var postViewModel: AddPostViewModel  // ViewModel instance
    private lateinit var groupdatabaseManager: GroupFirebaseManager
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null
    private lateinit var spinner: Spinner
    private val nameList = mutableListOf<String>()
    private val idList = mutableListOf<String>()
    private var selectedGroupId: String? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel directly using ViewModelProvider
        postViewModel = ViewModelProvider(this).get(AddPostViewModel::class.java)
        groupdatabaseManager= GroupFirebaseManager(this)

        // Retrieve the passed data (if any)
        post = intent.getParcelableExtra("post")

        // Set title and description if post exists
        post?.let {
            binding.homeTitleId.setText(it.title)
            binding.homeDescId.setText(it.desc)
            binding.spinner.visibility = View.GONE
            binding.imageView.visibility = View.VISIBLE

            it.imageUrl?.let { urlString ->
                Glide.with(this)
                    .load(urlString)
                    .into(binding.imageView)
            }
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
        getGroup()

        binding.selectImageButton.setOnClickListener {
            openImageChooser()
        }

        binding.homeButtonId.setOnClickListener {
            val title = binding.homeTitleId.text.toString().trim()
            val description = binding.homeDescId.text.toString().trim()

            if (imageUri != null) {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    if (post == null) {
                        savePost()
                    } else {
                        // Update existing post
                        post?.id?.let { postId ->
                            updatePost(postId)
                        }
                    }
                } else {
                    Toast.makeText(this, "Please fill in both title and description.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe the LiveData to get success/failure status
        postViewModel.isPostSaved.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }
        })
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
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getGroup() {
        groupdatabaseManager.getGroup { groups ->
            nameList.clear()
            idList.clear()
            groups.forEach { (name, id) ->
                nameList.add(name)
                idList.add(id)
            }
            setupSpinner()
        }
    }

    // Update Home Collection (this stays the same)
    private fun updatePost(postId: String) {
        binding.submitLoader.visibility = View.VISIBLE
        binding.homeButtonId.visibility = View.GONE

        val title = binding.homeTitleId.text.toString().trim()
        val description = binding.homeDescId.text.toString().trim()

        imageUri?.let { uri ->
            if (uri != null) {
                postViewModel.updateData(postId, uri, title, description)
            }
        }
    }

    // Save Data In home Collection
    private fun savePost() {
        binding.submitLoader.visibility = View.VISIBLE
        binding.homeButtonId.visibility = View.GONE

        val title = binding.homeTitleId.text.toString().trim()
        val desc = binding.homeDescId.text.toString().trim()
        val linkAddress = binding.link.text.toString().trim()

        imageUri?.let { uri ->
            postViewModel.saveData(uri, title, desc, selectedGroupId, isFavorite, linkAddress)
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




