package com.example.project01.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project01.R
import com.example.project01.databinding.ActivityChangePasswordBinding
import com.example.project01.dialogs.DialogUtils
import com.example.project01.firebase.FirebaseAuthManager
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var changePasswordButton: Button
    private val authManager = FirebaseAuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrovBack.setOnClickListener {
            finish()
        }

        currentPasswordEditText = findViewById(R.id.currentPasswordEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()

            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                performChangePassword(currentPassword, newPassword)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performChangePassword(currentPassword: String, newPassword: String) {
        binding.changePassworLoader.visibility = View.VISIBLE
        binding.changePasswordButton.visibility = View.GONE
//This scope will be cancelled when the Lifecycle is destroyed.
        lifecycleScope.launch {
            try {
                authManager.changePassword(currentPassword, newPassword)
                DialogUtils.ChangegePassSuccessDialog(this@ChangePasswordActivity) {
                    startActivity(
                        Intent(
                            this@ChangePasswordActivity,
                            ChangePasswordActivity::class.java
                        )
                    )
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ChangePasswordActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.changePassworLoader.visibility = View.GONE
                binding.changePasswordButton.visibility = View.VISIBLE
            }
        }
    }

}