package com.example.project01.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.project01.R
import com.example.project01.adaptor.GroupAdapter
import com.example.project01.databinding.FragmentUserProfileBinding
import com.example.project01.repositoryfirebase.UserFirebaseManager
import com.example.project01.firebaseold.GroupFirebaseManager
import com.example.project01.modal.GroupModal
import com.example.project01.viewmodal.GroupViewModel

class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var userId: String
    private var followedUserId: String? = null
    private lateinit var firebaseDatabaseManager: UserFirebaseManager
    private lateinit var groupdatabaseManger: GroupFirebaseManager
    private val itemList = mutableListOf<GroupModal>()
    private lateinit var groupRecyclerAdapter: GroupAdapter
    private var isFollowing: Boolean = false
    private lateinit var groupViewModel: GroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get id's from Home Fragment
        arguments?.let {
            // Set userId from HomeFragment
            userId = it.getString("userId", "")
        }

        firebaseDatabaseManager = UserFirebaseManager(requireContext())
        groupdatabaseManger= GroupFirebaseManager(requireContext())
        groupViewModel=ViewModelProvider(this).get(GroupViewModel::class)
        binding.groupRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.clickfollower.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right).build()

            findNavController().navigate(R.id.followersFragment, bundle, navOptions)
        }
        binding.followingClick.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
            }

            val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right).build()
            findNavController().navigate(R.id.followingFragment, bundle, navOptions)
        }
        binding.profiletoobar.setOnClickListener {


            findNavController().navigateUp()
        }

        if (userId.isNotEmpty()) {
            loadUserData(userId)
            groupViewModel.fetchGroups(userId)
            fetchFollowersCount(userId)
            fetchFollowingCount(userId)
            fetchGroupCount(userId)
            fetchFollowStatus(userId)
        }
        groupViewModel.group.observe(viewLifecycleOwner, Observer { fetchgroupList->

            setupRecyclerView(fetchgroupList)
        })

        binding.followButton.setOnClickListener {
            followedUserId?.let { id ->
                if (isFollowing) {
                    firebaseDatabaseManager.unfollowUser(id)
                    isFollowing = false
                } else {
                    firebaseDatabaseManager.followUser(id)
                    isFollowing = true
                }
                updateFollowButton()
            }
        }
    }

    // Update button based on follow status
    private fun fetchFollowStatus(userId: String) {
        firebaseDatabaseManager.isUserFollowed(userId) { followed ->
            isFollowing = followed
            updateFollowButton()
        }
    }

    private fun updateFollowButton() {
        binding.followButton.text = if (isFollowing) "Unfollow" else "Follow"
    }

    // Function to fetch followers count
    private fun fetchFollowersCount(userId: String) {
        firebaseDatabaseManager.getFollowersCount(userId) { count ->
            binding.followersCountTextView.text = count.toString()
        }
    }

    private fun fetchFollowingCount(userId: String) {
        firebaseDatabaseManager.geFollwingCount(userId) { count ->
            binding.followingCountTextView.text = count.toString()
        }

    }

    private fun fetchGroupCount(userId: String) {
        groupdatabaseManger.getGroupCount(userId) { count ->
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

        val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        findNavController().navigate(R.id.postFragment, bundle, navOptions)
    }

    // SetUp RecyclerView
    private fun setupRecyclerView(itemList:List<GroupModal>) {
        groupRecyclerAdapter = GroupAdapter(
            itemList = itemList,
            onItemClick = { item -> onItemClick(item) },
            isPopupMenuVisible = false
        )
        binding.groupRecyclerview.adapter = groupRecyclerAdapter
    }

    private fun loadUserData(userId: String) {
        firebaseDatabaseManager.getUser(userId) { username, email, profileImageUrl ->
            followedUserId = userId
            binding.usernameTextView.text = username ?: "Unknown User"

            if (profileImageUrl.isNullOrEmpty()) {
                binding.profileImageView.setImageResource(R.drawable.ic_defauluser) // Default image
            } else {
                Glide.with(this).load(profileImageUrl).circleCrop().into(binding.profileImageView)
            }
        }
    }


}
