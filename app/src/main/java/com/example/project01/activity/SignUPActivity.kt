package com.example.project01.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project01.databinding.ActivitySignUpactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUPActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpactivityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.arrovBack.setOnClickListener {
            finish()
        }
        firebaseAuth = FirebaseAuth.getInstance()

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
    private fun performSignUp(email: String, password: String) {
        binding.singnupLoader.visibility = View.VISIBLE
        binding.signupButton1.visibility = View.GONE
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    performSignUp2(task.result.user?.uid)
                    showSuccessDialog()
                } else {
                    showFailureDialog()
                }
            }
        Handler(Looper.getMainLooper()).postDelayed({

            binding.singnupLoader.visibility = View.GONE
        }, 1000)

    }
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("SignUp Successful")
            .setMessage("Welcome")
            .setPositiveButton("OK") { dialog, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                dialog.dismiss()
            }
            .show()
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("SignUp Failed")
            .setMessage("Incorrect email or password. Please try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performSignUp2(uid: String?) {
        if(uid == null) return

        val name = binding.signupName.text.toString().trim()
        val email = binding.singupEmail.text.toString().trim()
        val userMap = hashMapOf(
            "name" to name,
            "email" to email
        )
        db.collection("user").document(uid).set(userMap)
    }
}

