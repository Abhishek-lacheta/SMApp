package com.example.project01.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project01.R
import com.example.project01.databinding.ActivityChangePasswordBinding
import com.example.project01.dialogs.DialogUtils
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var changePasswordButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrovBack.setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()

            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                changePassword(currentPassword, newPassword)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        binding.changePassworLoader.visibility = View.VISIBLE
        binding.changePasswordButton.visibility = View.GONE
        user?.let {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            it.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    DialogUtils.ChangegePassSuccessDialog(this){
                                        startActivity(Intent(this,ChangePasswordActivity::class.java))
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Error: ${updateTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Reauthentication failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.changePassworLoader.visibility = View.GONE
        }, 1000)

    }

}