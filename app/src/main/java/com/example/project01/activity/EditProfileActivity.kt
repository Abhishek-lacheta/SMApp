package com.example.project01.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.project01.R
import com.example.project01.databinding.ActivityEditProfileBinding
import com.example.project01.repositoryfirebase.UserFirebaseManager


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var firebaseDatabaseManager: UserFirebaseManager
    private var profileImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabaseManager = UserFirebaseManager(this)
        loadUserData()

        binding.buttonEditProfile.setOnClickListener {
            pickImage()
        }

        binding.buttonSave.setOnClickListener {
            saveProfile()
        }

        binding.edittoolbar.setTitle("Edit Profile")
        binding.edittoolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadUserData() {
        firebaseDatabaseManager.getUser { username, email, imageUrl ->
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

        firebaseDatabaseManager.updateData(username, email, profileImageUri,
            onSuccess = {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Log.w("EditProfileActivity", "Error updating profile", e)
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
