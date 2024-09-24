package com.example.project01.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.project01.activity.ChangePasswordActivity
import com.example.project01.activity.EditProfileActivity
import com.example.project01.activity.LoginActivity
import com.example.project01.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        updateUI()

        binding.logoutId.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.editProfileButton.setOnClickListener {
            val intent=Intent(requireContext(),EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.ChangePassword.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)

        }
    }

    private fun updateUI() {
        val currentUser = auth.currentUser
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

                        // Load profile image (optional)
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

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun logout() {
        binding.logoutLoader.visibility = View.VISIBLE
        binding.logoutId.visibility = View.GONE

        auth.signOut()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.logoutLoader.visibility = View.GONE
            Toast.makeText(activity, "Logout successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }, 1000)
    }


}