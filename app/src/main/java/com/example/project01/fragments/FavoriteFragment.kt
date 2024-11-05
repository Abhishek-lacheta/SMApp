package com.example.project01.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.R
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentFavoriteBinding
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.HomeModal
import com.google.firebase.auth.FirebaseAuth

class FavoriteFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentFavoriteBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var databaseManager: FirebaseDatabaseManager
    private lateinit var noDataLayout: View

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
        noDataLayout = binding.noDataLayout

        fetchFavoriteData()
    }

    //comment ke liye
    fun onComment(modal: HomeModal) {

        val bundle = Bundle().apply {

            putString("postId", modal.id)
        }
        val bottomSheet = CommentsFragment().apply {
            arguments = bundle
        }

        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }


    private fun fetchFavoriteData() {
        databaseManager.fetchFavoriteItemsFromFirebase { fetchedList ->
            if (fetchedList.isEmpty()) {
                noDataLayout.visibility = View.VISIBLE
                binding.recyclerview.visibility = View.GONE
            } else {
                noDataLayout.visibility = View.GONE
                binding.recyclerview.visibility = View.VISIBLE
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

    //new comment count
    fun updateCommentCount(postId: String, newCount: Int) {
        val post = dataList.find { it.id == postId }
        post?.let {
            it.commentcount = newCount
            adapter.notifyItemChanged(dataList.indexOf(it)) // Notify adapter of changes
        }
    }

    private fun setupRecyclerView() {
        adapter = HomeAdaptor(
            itemList = dataList,
            currentUserId = null,
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
        val navOptions =
            NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build()
        findNavController().navigate(R.id.userProfileFragment, bundle,navOptions)
    }
}
