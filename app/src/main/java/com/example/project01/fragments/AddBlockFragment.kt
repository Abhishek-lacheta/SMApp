package com.example.project01.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.R
import com.example.project01.activity.AddPostHomeActivity
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentAddBloackBinding
import com.example.project01.dialogs.BottomSeatFragment
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.HomeModal
import com.google.firebase.auth.FirebaseAuth

class AddBlockFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentAddBloackBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var databaseManager: FirebaseDatabaseManager
    private lateinit var modalId: String
    private lateinit var modalName: String
    private lateinit var userId: String
    private lateinit var navController: NavController
    private val authManager = FirebaseAuthManager()
    private lateinit var noDataLayout: View


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBloackBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            modalId = it.getString("modalId", "")
            modalName = it.getString("name", "")
            userId = it.getString("userId", "")

        }

        databaseManager = FirebaseDatabaseManager(requireContext())
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        noDataLayout = binding.noDataLayout


        fetchDataFromFirestore()


        binding.AddBlocktoolbar.setTitle(modalName)
        binding.AddBlocktoolbar.menu.clear()
        navController = findNavController()
        binding.AddBlocktoolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
    }

    //Comment ke liye
    fun onComment(modal: HomeModal) {

        val bundle = Bundle().apply {

            putString("postId", modal.id)
        }
        val bottomSheet = BottomSeatFragment().apply {
            arguments = bundle
        }

        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    // fetch data on group fragment
    private fun fetchDataFromFirestore() {
        databaseManager.fetchDataByGroupId(userId, modalId) { fetchedList ->
            if (fetchedList.isEmpty()) {
                noDataLayout.visibility = View.VISIBLE
                binding.recyclerview.visibility = View.GONE
            } else {
                noDataLayout.visibility = View.GONE
                binding.recyclerview.visibility = View.VISIBLE
                dataList.clear() // Clear existing data
                dataList.addAll(fetchedList)
                setupRecyclerView() // Initialize the adapter
            }
        }
    }

    private fun toggleLike(item: HomeModal) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val newLikedStatus = !item.isLikedByCurrentUser // Toggle the like status

        databaseManager.toggleLike(item.id!!, currentUserId, newLikedStatus) { success ->
            if (success) {
                // Update local state based on newLikedStatus
                if (newLikedStatus) {
                    // User just liked the post
                    item.likedBy = item.likedBy + currentUserId
                    item.likeCount += 1
                } else {
                    // User just unliked the post
                    item.likedBy = item.likedBy.filter { id -> id != currentUserId }
                    item.likeCount -= 1
                }
                adapter.notifyItemChanged(dataList.indexOf(item)) // Notify adapter of changes
            } else {
                Toast.makeText(context, "Failed to update like", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        val currentUserId = authManager.getCurrentUser()?.uid // Get the current user's UID
        adapter = HomeAdaptor(
            itemList = dataList,
            onShowPopupMenu = { view, item -> showPopupMenu(view, item) },
            currentUserId = currentUserId,
            onLikeClick = { item -> toggleLike(item) },// Pass the like click handler
            onComment = { item -> onComment(item) },
            onItemClick = { item -> onItemClick(item) }
        )
        binding.recyclerview.adapter = adapter
    }

    fun onItemClick(modal: HomeModal) {
        val bundle = Bundle().apply {
            putString("userId", modal.userId)
        }
        findNavController().navigate(R.id.userProfileFragment, bundle)
    }
    private fun deleteItem(item: HomeModal): Boolean {
        item.id?.let { documentId ->
            databaseManager.deleteData(documentId) { success ->
                if (success) {
                    dataList.remove(item)
                    adapter.notifyDataSetChanged()
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

    private fun showPopupMenu(view: View, item: HomeModal) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.confirm_delete -> deleteItem(item)
                R.id.update -> {
                    val intent = Intent(requireContext(), AddPostHomeActivity::class.java).apply {
                        putExtra("post", item)
                    }
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
}

