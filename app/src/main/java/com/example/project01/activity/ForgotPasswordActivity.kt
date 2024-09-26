package com.example.project01.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project01.databinding.ActivityForgotPasswordBinding
import com.example.project01.dialogs.DialogUtils
import com.example.project01.firebase.FirebaseAuthManager
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val authManager = FirebaseAuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth

        binding.resetPasswordButton.setOnClickListener {
            val email = binding.Email.text.toString().trim()
            if (validateInput(email)) {
                sendPasswordResetEmail(email)
            }
        }
    }
    private fun sendPasswordResetEmail(email: String) {
        binding.forgotPassworLoader.visibility = View.VISIBLE
        binding.resetPasswordButton.visibility = View.GONE
        lifecycleScope.launch {
            try {
                authManager.forgotpassword(email)
                DialogUtils.ForgotSuccessDialog(this@ForgotPasswordActivity)
                startActivity(Intent(this@ForgotPasswordActivity, ForgotPasswordActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@ForgotPasswordActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.forgotPassworLoader.visibility = View.GONE
                binding.resetPasswordButton.visibility=View.VISIBLE
            }
        }
    }
    private fun validateInput(email: String): Boolean {

        return when {
            email.isEmpty() -> {
                binding.Email.error = "Enter email"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.Email.error = "Enter a valid email"
                false
            }
            else -> true
        }
    }
}