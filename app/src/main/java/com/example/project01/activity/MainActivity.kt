package com.example.project01.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.project01.R
import com.example.project01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.Fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        // Set up the circular button click listener
        binding.circularButton.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }


    }
}
