package com.example.project01.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.example.project01.R
import com.example.project01.databinding.ActivityAddPostHomeBinding
import com.example.project01.firebase.FirebaseDatabaseManager

class AddPostHomeActivity : AppCompatActivity() {

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
        val itemTitle = intent.getStringExtra("itemtitle")
        val itemDescription = intent.getStringExtra("itemdes")

        // Set the data to EditTexts
      binding.homeTitleId.setText(itemTitle)
        binding.homeDescId.setText(itemDescription)

        binding.hometoolbar.setTitle("Add Post")
        binding.hometoolbar.setNavigationOnClickListener {
            finish()
        }
        spinner = findViewById(R.id.spinner)
        fetchData()

        binding.selectImageButton.setOnClickListener {
            openImageChooser()
        }
        binding.homeButtonId.setOnClickListener {
            if (imageUri != null) {
                uploadImageAndSaveData()
            } else {
                // Optionally handle saving without image
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
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
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

    private fun uploadImageAndSaveData() {
        binding.submitLoader.visibility = View.VISIBLE
        binding.homeButtonId.visibility = View.GONE

        val title = binding.homeTitleId.text.toString().trim()
        val desc = binding.homeDescId.text.toString().trim()

        imageUri?.let { uri ->
            databaseManager.uploadImageAndSaveData(uri, title, desc, selectedGroupId, isFavorite) { success ->
                binding.submitLoader.visibility = View.GONE
                binding.homeButtonId.visibility = View.VISIBLE

            }
        }
    }
}


