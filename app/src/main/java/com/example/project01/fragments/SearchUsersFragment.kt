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
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.FollowerModal
import com.google.firebase.firestore.FirebaseFirestore

class SearchUsersFragment : Fragment() {
    private lateinit var binding: FragmentSearchUsersBinding
    private val itemList = mutableListOf<FollowerModal>()
    private lateinit var followerAdaptor: FollowerAdaptor
    private val database = FirebaseFirestore.getInstance()
    private lateinit var databaseManager: FirebaseDatabaseManager
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
        databaseManager = FirebaseDatabaseManager(requireContext())
        binding.followerrecyclerView.layoutManager = LinearLayoutManager(context)
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        followerAdaptor = FollowerAdaptor(followers = itemList) { item ->
            // Handle item click
        }
        binding.followerrecyclerView.adapter = followerAdaptor
    }

   /* override fun search(query: String) {
        if (query.isNotEmpty()) {

            databaseManager.searchUsers(query) { fetchedList ->
                itemList.clear()
                itemList.addAll(fetchedList)
                followerAdaptor.notifyDataSetChanged()
            }

        }
    }*/
}
