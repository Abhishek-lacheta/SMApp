package com.example.project01.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project01.databinding.ActivityAddHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.project01.R
import com.example.project01.modal.Item
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.HashMap

class AddHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null

    private lateinit var spinner: Spinner
    private val nameList = mutableListOf<String>()
    private val idList = mutableListOf<String>()
    private var selectedGroupId: String? = null  // To store the selected group ID
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                /*saveDataWithoutImage()*/
            }
        }
    }

    private fun fetchData() {
        db.collection("group").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    nameList.add(document.getString("name") ?: "")
                    idList.add(document.id)
                }
                setupSpinner()
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting documents: ", exception)
            }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nameList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedGroupId = idList[position]  // Store the selected group ID
                Log.d("MainActivity", "Selected Group ID: $selectedGroupId")
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

        val homeMap = hashMapOf(
            "title" to title,
            "desc" to desc,
            "created_at" to FieldValue.serverTimestamp(),
            "groupId" to (selectedGroupId ?: ""), // Add groupId to the homeMap
            "isFavorite" to isFavorite  // Add favorite status to homeMap


        )
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        imageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        homeMap["imageUrl"] = downloadUri.toString()
                        saveHomeData(homeMap)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.submitLoader.visibility = View.GONE
        }, 1000)

    }
    private fun saveHomeData(homeMap: HashMap<String, Any>) {
        db.collection("home").document().set(homeMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully Added Data", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add data", Toast.LENGTH_SHORT).show()
            }
    }
}


