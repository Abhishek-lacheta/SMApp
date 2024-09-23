package com.example.project01.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project01.databinding.ActivityAddGroupBinding
import com.example.project01.databinding.ActivityAddHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.Grouptoolbar.setTitle("Add Post")
        binding.Grouptoolbar.setNavigationOnClickListener {
            finish()
        }

        binding.selectImageButton.setOnClickListener {
            openImageChooser()
        }
        binding.buttonId.setOnClickListener {

            if (imageUri != null) {

                uploadImageAndSaveData()
            }

        }

    }


    private fun uploadImageAndSaveData() {
        val name = binding.nameId.text.toString().trim()
        val homeMap = hashMapOf(
            "name" to name,
            //"created_at" to FieldValue.serverTimestamp()

        )

        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        imageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        homeMap["imageUrl"] = downloadUri.toString()
                        saveGroupData(homeMap)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
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

    private fun saveGroupData(homeMap: HashMap<String, String>) {
        db.collection("group").document().set(homeMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully Added Data", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add data", Toast.LENGTH_SHORT).show()
            }
    }
}