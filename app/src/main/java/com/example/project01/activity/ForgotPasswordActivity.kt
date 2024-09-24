package com.example.project01.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project01.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

       binding.resetPasswordButton.setOnClickListener {
            val email = binding.Email.text.toString().trim()
            if (validateInput(email)) {
                sendPasswordResetEmail(email)
            }
        }
    }
    private fun sendPasswordResetEmail(email: String) {
        binding.forgotPassworLoader.visibility=View.VISIBLE
        binding.resetPasswordButton.visibility=View.GONE
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showSuccessDialog()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.forgotPassworLoader.visibility = View.GONE
        }, 1000)
    }
    private fun validateInput(email: String ): Boolean {

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
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Please check your email")
            .setMessage("Provide you link to reset your password")
            .setPositiveButton("OK") { dialog, _ ->
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
                finish()
                dialog.dismiss()
            }
            .show()
    }
}