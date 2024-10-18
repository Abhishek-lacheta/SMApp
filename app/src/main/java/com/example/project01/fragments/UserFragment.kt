package com.example.project01.fragments

import GroupRecyclerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.project01.R
import com.example.project01.activity.AddPostGroupActivity
import com.example.project01.activity.ChangePasswordActivity
import com.example.project01.activity.EditProfileActivity
import com.example.project01.activity.LoginActivity
import com.example.project01.activity.SettingActivity
import com.example.project01.databinding.FragmentUserBinding
import com.example.project01.dialogs.DialogUtils
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.GroupModal


class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private var authManager = FirebaseAuthManager()
    val currentUser = authManager.getCurrentUser()
    val userId = currentUser?.uid
    private lateinit var groupRecyclerAdapteradaptor: GroupRecyclerAdapter
    private val itemList = mutableListOf<GroupModal>()
    private lateinit var databaseManager: FirebaseDatabaseManager
    private lateinit var noDataLayout: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val activity = activity as AppCompatActivity
        val toolbar: Toolbar = binding.toolbar
        activity.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        // Initialize FirebaseDatabaseManager
        databaseManager = FirebaseDatabaseManager(requireContext())

        // Set up RecyclerView
        binding.groupRecyclerview.layoutManager = GridLayoutManager(context, 2)
        noDataLayout = binding.noDataLayout
        // Fetch data from Firestore
        if (userId != null) {
            fetchGroupData(userId)
        }
        updateUI()

        //Open Follwers Screen
        binding.clickfollower.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            val navOptions =
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()
            findNavController().navigate(R.id.followersFragment, bundle, navOptions)
        }
        //Open Following screen
        binding.followingClick.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            val navOptions =
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()
            findNavController().navigate(R.id.followingFragment, bundle, navOptions)
        }

        binding.GroupClick.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("openFromUserfragment", true)
            }
            val navOptions =
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()
            findNavController().navigate(R.id.groupsFragment, bundle, navOptions)
        }

        if (userId != null) {
            fetchFollowersCount(userId)
            fetchFollowingCount(userId)
            fetchGroupCount(userId)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clicksetting-> {
                val intent = Intent(requireContext(), SettingActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Function to fetch followers count
    private fun fetchFollowersCount(userId: String) {
        databaseManager.getFollowersCount(userId) { count ->
            binding.followersCountTextView.text =
                count.toString()
        }
    }

    // Function to fetch following count
    private fun fetchFollowingCount(userId: String) {
        databaseManager.geFollwingCount(userId) { count ->
            binding.followingCountTextView.text = count.toString()
        }

    }

    // Function to fetch group count
    private fun fetchGroupCount(userId: String) {
        databaseManager.fetchGroupCountFromFirestore(userId) { count ->
            binding.groupCountTextView.text = count.toString()
        }
    }
    //Get User Data
    private fun updateUI() {
        val currentUser = authManager.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid

            // Use FirebaseDataManager to fetch user data
            databaseManager.getUserData(userId) { username, email, profileImageUrl ->
                binding.userEmail.text = email ?: "No email"
                binding.name.text = username ?: "No username"

                profileImageUrl?.let {
                    Glide.with(this)
                        .load(it)
                        .transform(CircleCrop())
                        .into(binding.profileImageView)
                } ?: run {
                    binding.profileImageView.setImageResource(R.drawable.ic_defauluser) // Set default image
                }
            }
        }
    }
    //SetUp RecyclerView
    private fun setupRecyclerView() {
        groupRecyclerAdapteradaptor = GroupRecyclerAdapter(
            itemList = itemList,
            onGroupPopupMenu = { view, item -> showPopupMenu(view, item) },
            onItemClick = { itemList -> onItemClick(itemList) },
            isPopupMenuVisible = true
        )
        binding.groupRecyclerview.adapter = groupRecyclerAdapteradaptor
    }

    private fun showPopupMenu(view: View, item: GroupModal) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.confirm_delete -> deleteItem(item)
                R.id.update -> {
                    val intent = Intent(requireContext(), AddPostGroupActivity::class.java).apply {
                        putExtra("group", item)
                    }
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    //delete data
    private fun deleteItem(item: GroupModal): Boolean {
        item.id?.let { documentId ->
            databaseManager.deleteGroupData(documentId) { success ->
                if (success) {
                    itemList.remove(item)
                    groupRecyclerAdapteradaptor.notifyDataSetChanged()
                    Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(context, "Item ID is null", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    //Navigate to AddBloackFragment
    fun onItemClick(model: GroupModal) {
        val bundle = Bundle().apply {
            putString("modalId", model.id)
            putString("name", model.name)
            putString("userId", authManager.getCurrentUser()?.uid)
            Log.d(
                "groupFragment",
                "Item clicked: ${model.id}, ${model.name},${authManager.getCurrentUser()?.uid}"
            )
        }
        val navOptions =
            NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build()

        findNavController().navigate(R.id.addBlockFragment, bundle, navOptions)
    }
    //Fetch User Data
    private fun fetchGroupData(userId: String) {
        databaseManager.fetchDataGroupFromeFireStore1(userId) { fetchedList ->
            itemList.clear()
            itemList.addAll(fetchedList)
            setupRecyclerView()
        }
    }
}
