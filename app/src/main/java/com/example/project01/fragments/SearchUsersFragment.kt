package com.example.project01.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.R
import com.example.project01.adaptor.FollowerAdaptor
import com.example.project01.databinding.FragmentSearchUsersBinding
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.FollowerModal
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class SearchUsersFragment : Fragment() {
    private lateinit var binding: FragmentSearchUsersBinding
    private val itemList = mutableListOf<FollowerModal>()
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager
    private lateinit var followerAdaptor: FollowerAdaptor
    private val database: FirebaseFirestore = Firebase.firestore
    private var authManager = FirebaseAuthManager()
    val currentUser = authManager.getCurrentUser()
    val userId = currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.followerrecyclerView.layoutManager = LinearLayoutManager(context)

        loadFollowerList()

    }

    fun loadFollowerList() {

        Log.d("LoadFollowerList", "Current user ID: $userId")
        if (userId != null) {
            database.collection("user").document(userId).collection("followers").get()
                .addOnSuccessListener { documents ->
                    Log.d("LoadFollowerList", "Successfully retrieved followers.")
                    val followers = mutableListOf<FollowerModal>()
                    for (document in documents) {
                        val userName = document.getString("userName") ?: "Unknown User"
                        val followerImage = document.getString("image")

                        followers.add(FollowerModal(userName, followerImage))
                        setuprecyclerView()
                    }
                    followerAdaptor.setFollowers(followers)

                }
        }
    }
    private fun setuprecyclerView() {
        followerAdaptor = FollowerAdaptor(
            followers = itemList,
            onItemClick = { itemList -> (itemList) },
        )
        binding.followerrecyclerView.adapter = followerAdaptor
    }

}