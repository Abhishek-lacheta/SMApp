package com.example.project01.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.FollowerAdaptor
import com.example.project01.databinding.FragmentSearchUsersBinding
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.modal.FollowerModal
import com.google.firebase.firestore.FirebaseFirestore

class SearchUsersFragment : Fragment(), Searchable {
    private lateinit var binding: FragmentSearchUsersBinding
    private val itemList = mutableListOf<FollowerModal>()
    private lateinit var followerAdaptor: FollowerAdaptor
    private val database = FirebaseFirestore.getInstance()
    private var authManager = FirebaseAuthManager()
    private val currentUser = authManager.getCurrentUser()
    private val userId = currentUser?.uid

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
        setupRecyclerView()
        getUserlist()
    }

    private fun setupRecyclerView() {
        followerAdaptor = FollowerAdaptor(followers = itemList) { item ->
            // Handle item click
        }
        binding.followerrecyclerView.adapter = followerAdaptor
    }

    private fun getUserlist() {
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
                    }
                    itemList.clear()
                    itemList.addAll(followers)
                    followerAdaptor.notifyDataSetChanged()
                }
        }
    }

    override fun search(query: String) {
        val filteredList = if (query.isEmpty()) {
            itemList // Return the full list if the query is empty
        } else {
            itemList.filter { it.userName.contains(query, ignoreCase = true) }
        }
        followerAdaptor.setFollowers(filteredList)
    }
}
