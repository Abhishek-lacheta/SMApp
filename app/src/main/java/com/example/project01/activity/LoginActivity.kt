package com.example.project01.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project01.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }

        binding.SendtoSinUp.setOnClickListener {
            startActivity(Intent(this, SignUPActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {

        return when {
            email.isEmpty() -> {
                binding.loginEmail.error = "Enter email"
                false
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.loginEmail.error = "Enter a valid email"
                false
            }

            password.isEmpty() -> {
                binding.loginPassword.error = "Enter password"
                false
            }

            password.isEmpty() || password.length < 6 -> {
                binding.loginPassword.error = "Password must be at least 6 characters"
                false
            }

            else -> true
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Successful")
            .setMessage("Welcome")
            .setPositiveButton("OK") { dialog, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                dialog.dismiss()
            }
            .show()
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Failed")
            .setMessage("Incorrect email or password. Please try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogin(email: String, password: String) {
        binding.loginLoader.visibility = View.VISIBLE
        binding.loginButton.visibility = View.GONE
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showSuccessDialog()
                } else {
                    showFailureDialog()
                }
            }

        Handler(Looper.getMainLooper()).postDelayed({

            binding.loginLoader.visibility = View.GONE
        }, 1000)
    }

}










