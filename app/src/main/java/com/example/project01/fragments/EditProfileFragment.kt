package com.example.project01.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.project01.R
import com.example.project01.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private var profileImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { 
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        loadUserData()

        binding.buttonChangeImage.setOnClickListener {
            pickImage()
        }

        binding.buttonSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = db.collection("user").document(userId)

            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("name")
                    val email = document.getString("email")
                    val imageUrl = document.getString("profileImageUrl")

                    binding.editTextUsername.setText(username)
                    binding.editTextEmail.setText(email)

                    // Load profile image if available or set a default image
                    if (imageUrl.isNullOrEmpty()) {
                        binding.profileImageView.setImageResource(R.drawable.ic_defauluser) // Default image
                    } else {
                        Glide.with(this)
                            .load(imageUrl)
                            .circleCrop() // Make image circular
                            .into(binding.profileImageView)
                    }
                }
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            profileImageUri = data?.data
            // Load selected image with Glide and make it circular
            profileImageUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .circleCrop() // Make image circular
                    .into(binding.profileImageView)
            }
        }
    }

    private fun saveProfile() {
        val username = binding.editTextUsername.text.toString()
        val email = binding.editTextEmail.text.toString()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = db.collection("user").document(userId)

            userRef.update("name", username, "email", email)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    // Handle image upload if needed
                    profileImageUri?.let { uri ->
                        uploadImageToStorage(uri, userId)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("EditProfileFragment", "Error updating profile", e)
                }
        }
    }

    private fun uploadImageToStorage(uri: Uri, userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$userId.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val userRef = db.collection("user").document(userId)
                    userRef.update("profileImageUrl", downloadUri.toString())
                        .addOnSuccessListener {
                            Log.d("EditProfileFragment", "Profile image updated")
                        }
                        .addOnFailureListener { e ->
                            Log.w("EditProfileFragment", "Error updating profile image", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("EditProfileFragment", "Error uploading image", e)
            }
    }
}
