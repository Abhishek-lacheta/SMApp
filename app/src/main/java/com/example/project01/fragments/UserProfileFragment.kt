package com.example.project01.fragments

import GroupRecyclerAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.project01.R
import com.example.project01.databinding.FragmentUserProfileBinding
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.GroupModal
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var userId: String
    private var followedUserId: String? = null
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager
    private val itemList = mutableListOf<GroupModal>()
    private lateinit var groupRecyclerAdapter: GroupRecyclerAdapter
    private var isFollowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            // Set userId from HomeFragment
            userId = it.getString("userId", "")
        }

        firebaseDatabaseManager = FirebaseDatabaseManager(requireContext())
        binding.groupRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.arrovBack.setOnClickListener {
            findNavController().navigateUp()
        }

        if (userId.isNotEmpty()) {
            loadUserData(userId)
            fetchGroupData(userId)
            fetchFollowersCount(userId)
            fetchFollowingCount(userId)
            fetchGroupCount(userId)
        }

        binding.followButton.setOnClickListener {
            followedUserId?.let { id ->
                if (isFollowing) {
                    firebaseDatabaseManager.unfollowUser(id)
                    isFollowing = false
                    binding.followButton.text = "Follow" // Update button text
                } else {
                    firebaseDatabaseManager.followUser(id)
                    isFollowing = true
                    binding.followButton.text = "UnFollow" // Update button text
                }
            }
        }
        binding.followButton.text = if (isFollowing) "UnFollow" else "Follow"
    }

    // Function to fetch followers count
    private fun fetchFollowersCount(userId: String) {
        firebaseDatabaseManager.getFollowersCount(userId) { count ->
            binding.followersCountTextView.text =
                count.toString()
        }
    }

    private fun fetchFollowingCount(userId: String) {
        firebaseDatabaseManager.geFollwingCount(userId) { count ->
            binding.followingCountTextView.text = count.toString()
        }

    }

    private fun fetchGroupCount(userId: String) {
        firebaseDatabaseManager.fetchGroupCountFromFirestore(userId) { count ->
            binding.groupCountTextView.text = count.toString()
        }
    }

    fun onItemClick(model: GroupModal) {
        val bundle = Bundle().apply {
            putString("modalId", model.id)
            putString("name", model.name)
            putString("userId", userId)
            Log.d("UserProfileFragment", "Item clicked: ${model.id}, ${model.name}, ${userId}")
        }
        findNavController().navigate(R.id.addBlockFragment, bundle)
    }

    // SetUp RecyclerView
    private fun setupRecyclerView() {
        groupRecyclerAdapter = GroupRecyclerAdapter(
            itemList = itemList,
            onItemClick = { item -> onItemClick(item) },
            isPopupMenuVisible = false
        )
        binding.groupRecyclerview.adapter = groupRecyclerAdapter
    }

    private fun loadUserData(userId: String) {
        firebaseDatabaseManager.loadUserData(userId) { username, email, imageUrl ->
            followedUserId = userId // Set the followed user ID here
            binding.usernameTextView.text = username ?: "Unknown User"

            if (imageUrl.isNullOrEmpty()) {
                binding.profileImageView.setImageResource(R.drawable.ic_defauluser) // Default image
            } else {
                Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .into(binding.profileImageView)
            }
        }
    }

    private fun fetchGroupData(userId: String) {
        firebaseDatabaseManager.fetchDataGroupFromeFireStore1(userId) { fetchedList ->
            itemList.clear()
            itemList.addAll(fetchedList)
            setupRecyclerView()
        }
    }

}
