package com.example.project01.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project01.databinding.ActivitySignUpactivityBinding
import com.example.project01.dialogs.DialogUtils
import com.example.project01.repositoryfirebase.FirebaseAuthManager
import kotlinx.coroutines.launch

class SignUPActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpactivityBinding
    private val authManager = FirebaseAuthManager()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrovBack.setOnClickListener {
            finish()
        }
        binding.signupButton1.setOnClickListener {
            val email = binding.singupEmail.text.toString()
            val pass = binding.signupPass.text.toString()
            if (validateInput(email, pass)) {
                performSignUp(email, pass)
            }
        }
    }
    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.singupEmail.error = "Enter email"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.singupEmail.error = "Enter a valid email"
                false
            }
            password.isEmpty() -> {
                binding.signupPass.error = "Enter password"
                false
            }
            password.isEmpty() || password.length < 6 -> {
                binding.signupPass.error = "Password must be at least 6 characters"
                false
            }

            else -> true
        }
    }
    //this is create user with eamil and password and store firebase
    private fun performSignUp(email: String, password: String) {
        binding.singnupLoader.visibility = View.VISIBLE
        binding.signupButton1.visibility = View.GONE

        val name = binding.signupName.text.toString()
        lifecycleScope.launch {
            try {
                authManager.registerUser(name, email, password)
                DialogUtils.signupSuccessDialog(this@SignUPActivity)
                startActivity(Intent(this@SignUPActivity, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                DialogUtils.signupFailureDialog(this@SignUPActivity)
            } finally {
                binding.singnupLoader.visibility = View.GONE
                binding.signupButton1.visibility=View.VISIBLE
            }
        }
    }
}

