package com.example.project01.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project01.R
import com.example.project01.databinding.ActivitySettingBinding
import com.example.project01.dialogs.DialogUtils
import com.example.project01.firebase.FirebaseAuthManager


class SettingActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingBinding
    private var authManager = FirebaseAuthManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      binding=ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Go to Edit Profile Activity
        binding.editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Go to Change Password Activity
        binding.ChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        binding.logoutId.setOnClickListener {
            logout()
        }
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun logout() {
        binding.logoutLoader.visibility = View.VISIBLE
        binding.logoutId.visibility = View.GONE

        authManager.signout {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.logoutLoader.visibility = View.GONE
                this?.let {
                    DialogUtils.LogoutConfirmationDialog(it) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        this?.finish()
                    }
                }
            }, 1000)
        }
    }


}