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
import com.example.project01.adaptor.FollowerAdaptor
import com.example.project01.databinding.FragmentFollowersBinding
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.FollowerModal
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FollowersFragment : Fragment() {
    private lateinit var binding: FragmentFollowersBinding
    private val itemList = mutableListOf<FollowerModal>()
    private val database: FirebaseFirestore = Firebase.firestore
    private lateinit var userId: String
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager
    private lateinit var navController: NavController
    private lateinit var followerAdaptor: FollowerAdaptor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //get userID from UserProfileFragment
        //get current userId from userFragment

        firebaseDatabaseManager = FirebaseDatabaseManager(requireContext())
        arguments?.let {
            userId = it.getString("userId", "")
        }
        navController = findNavController()
        binding.Followerstoolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
        binding.followerrecyclerView.layoutManager = LinearLayoutManager(context)

        loadFollowerList()
    }

    private fun setuprecyclerView() {
        followerAdaptor = FollowerAdaptor(
            followers = itemList,
            onItemClick = { itemList -> onItemClick(itemList) },
        )
        binding.followerrecyclerView.adapter = followerAdaptor
    }

    private fun onItemClick(follower: FollowerModal) {
        val isFollowing = follower.isFollowed
        follower.isFollowed = !isFollowing // Toggle follow status

        if (isFollowing) {
            firebaseDatabaseManager.unfollowUser(userId)
        } else {
            firebaseDatabaseManager.followUser(userId)
        }

        // Update the RecyclerView
        //Position of the item that has changed
        followerAdaptor.notifyItemChanged(itemList.indexOf(follower))
    }


    fun loadFollowerList() {

        Log.d("LoadFollowerList", "Current user ID: $userId")
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
