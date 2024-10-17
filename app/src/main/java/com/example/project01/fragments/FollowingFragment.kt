package com.example.project01.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.FollowingAdaptor
import com.example.project01.databinding.FragmentFollowingBinding
import com.example.project01.modal.FollowingModal
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class FollowingFragment : Fragment() {
    private lateinit var binding: FragmentFollowingBinding
    private val followingAdaptor = FollowingAdaptor(mutableListOf())
    private val database: FirebaseFirestore = Firebase.firestore
    private lateinit var userId: String
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            userId = it.getString("userId", "")
        }
        navController = findNavController()
        binding.Followingtoolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
        binding.followingrecyclerView.layoutManager = LinearLayoutManager(context)
        binding.followingrecyclerView.adapter = followingAdaptor
        loadFollowingList()
    }

    fun loadFollowingList() {

        Log.d("LoadFollowerList", "Current user ID: $userId")
        database.collection("user").document(userId).collection("following").get()
            .addOnSuccessListener { documents ->
                Log.d("LoadFollowerList", "Successfully retrieved followers.")
                val follwings = mutableListOf<FollowingModal>()
                for (document in documents) {
                    val userName = document.getString("userName") ?: "Unknown User"
                    val followerImage = document.getString("image")

                    follwings.add(FollowingModal(userName, followerImage))
                }
                followingAdaptor.setFollowings(follwings)
            }
    }


}