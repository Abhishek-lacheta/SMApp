package com.example.project01.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.project01.activity.ChangePasswordActivity
import com.example.project01.activity.EditProfileActivity
import com.example.project01.activity.LoginActivity
import com.example.project01.databinding.FragmentUserBinding
import com.example.project01.dialogs.DialogUtils
import com.example.project01.firebase.FirebaseAuthManager
import com.google.firebase.firestore.FirebaseFirestore


class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val db = FirebaseFirestore.getInstance()
    private var authManager = FirebaseAuthManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = FirebaseAuthManager()
        updateUI()

        binding.logoutId.setOnClickListener {
            logout()
        }
        //Go to Edit profile Activity
        binding.editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
        //Go to ChangePassword Activity
        binding.ChangePassword.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)

        }
    }
    private fun updateUI() {
        val currentUser = authManager.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = db.collection("user").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("name")
                        val email = document.getString("email")
                        val profileImageUrl = document.getString("profileImageUrl")

                        // Display the retrieved values
                        binding.userEmail.text = email ?: "No email"
                        binding.name.text = username ?: "No username"

                        profileImageUrl?.let {
                            Glide.with(this)
                                .load(it)
                                .transform(CircleCrop())
                                .into(binding.profileImageView)
                        }
                    } else {
                        binding.userEmail.text = currentUser.email ?: "No email"
                        binding.name.text = "No username"
                    }
                }
        }
    }
    private fun logout() {
        binding.logoutLoader.visibility = View.VISIBLE
        binding.logoutId.visibility = View.GONE

        authManager.signout {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.logoutLoader.visibility = View.GONE
                context?.let {
                    DialogUtils.LogoutConfirmationDialog(it) {
                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                }

            }, 1000)

        }
    }
}