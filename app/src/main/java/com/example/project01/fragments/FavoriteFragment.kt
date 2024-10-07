package com.example.project01.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.R
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentFavoriteBinding
import com.example.project01.dialogs.BottomSeatFragment
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.HomeModal
import com.google.firebase.auth.FirebaseAuth


class FavoriteFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentFavoriteBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var databaseManager: FirebaseDatabaseManager
    private val authManager = FirebaseAuthManager()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseManager = FirebaseDatabaseManager(requireContext())
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        fetchFavoriteData()
    }

    //comment ke liye
    fun onComment(modal: HomeModal) {

        val bundle = Bundle().apply {

            putString("postId", modal.id)
        }
        val bottomSheet = BottomSeatFragment().apply {
            arguments = bundle
        }

        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }


    private fun fetchFavoriteData() {
        databaseManager.fetchFavoriteItemsFromFirebase { fetchedList ->
            if (fetchedList.isEmpty()) {
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
            } else {
                dataList.clear()
                dataList.addAll(fetchedList)
                setupRecyclerView()
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
            onShowPopupMenu = { view, item -> /* Handle popup menu */ },
            currentUserId = currentUserId,
            onLikeClick = { item -> toggleLike(item) },// Pass the like click handler
            onComment = { item -> onComment(item) }
        )
        binding.recyclerview.adapter = adapter
    }
}
