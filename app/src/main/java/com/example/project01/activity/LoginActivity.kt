package com.example.project01.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project01.databinding.ActivityLoginBinding
import com.example.project01.dialogs.DialogUtils
import com.example.project01.repositoryfirebase.FirebaseAuthManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authManager = FirebaseAuthManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


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

    private fun performLogin(email: String, password: String) {
        binding.loginLoader.visibility = View.VISIBLE
        binding.loginButton.visibility = View.GONE

        lifecycleScope.launch {
            try {
                authManager.signInUser(email, password)
                DialogUtils.loginSuccessDialog(this@LoginActivity) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                DialogUtils.loginFailureDialog(this@LoginActivity)
            } finally {
                binding.loginLoader.visibility = View.GONE
                binding.loginButton.visibility = View.VISIBLE
            }
        }
    }


}










