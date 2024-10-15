package com.example.project01.fragments

import GroupRecyclerAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.project01.R
import com.example.project01.databinding.FragmentUserProfileBinding
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.GroupModal
import com.example.project01.modal.HomeModal


class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var userId: String
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager
    private val itemList = mutableListOf<GroupModal>()
    private lateinit var groupRecyclerAdapteradaptor: GroupRecyclerAdapter


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
            //set userId form HomeFragment
            userId = it.getString("userId", "")

        }

        firebaseDatabaseManager = FirebaseDatabaseManager(requireContext())
        binding.groupRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)


        binding.arrovBack.setOnClickListener {
            findNavController().navigateUp()
        }
        if (userId != null) {
            loadUserData(userId)
            fetchGroupData(userId)
        }
    }

    fun onItemClick(model: GroupModal) {

        val bundle = Bundle().apply {
            putString("modalId", model.id)
            putString("name", model.name)
            putString("userId", userId)
            Log.d("UserProfileFragment", "Item clicked: ${model.id}, ${model.name},${userId}")
        }
        findNavController().navigate(R.id.addBlockFragment, bundle)

    }

    //SetUp RecyclerView
    private fun setupRecyclerView() {
        groupRecyclerAdapteradaptor = GroupRecyclerAdapter(
            itemList = itemList,
            onItemClick = { itemList -> onItemClick(itemList) },
            isPopupMenuVisible = false
        )
        binding.groupRecyclerview.adapter = groupRecyclerAdapteradaptor
    }

    private fun loadUserData(userId: String) {
        firebaseDatabaseManager.loadUserData(userId) { username, email, imageUrl ->
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