package com.example.project01.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project01.R
import com.example.project01.databinding.ActivityUserProfileBinding


class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.arrovBack.setOnClickListener {
            finish()
        }

    }
}