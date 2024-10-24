package com.example.project01.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.modal.HomeModal
import com.example.project01.R
import com.example.project01.databinding.FragmentHomeBinding
import com.example.project01.firebase.FirebaseDatabaseManager
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentHomeBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager
    private lateinit var noDataLayout: View

    // Filter function
    fun filter(query: String) {
        val filteredList = dataList.filter { item ->
            item.title?.contains(query, ignoreCase = true) == true

        }
        adapter.updateList(filteredList)
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseDatabaseManager
        firebaseDatabaseManager = FirebaseDatabaseManager(requireContext())


        // Setup RecyclerView
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        noDataLayout = binding.noDataLayout

        // Fetch Data from Firestore
        fetchHomeData()
    }

    //fetch home data
    private fun fetchHomeData() {
        firebaseDatabaseManager.fetchDataHomeFromFireStore { fetchedList ->
            if (fetchedList.isEmpty()) {
                noDataLayout.visibility = View.VISIBLE
                binding.recyclerview.visibility = View.GONE

            } else {
                noDataLayout.visibility = View.GONE
                binding.recyclerview.visibility = View.VISIBLE
                dataList.clear()
                dataList.addAll(fetchedList)
            }

            // Fetch comment counts for each post
            dataList.forEach { item ->
                item.id?.let {
                    firebaseDatabaseManager.getCommentCountForPost(it) { count ->
                        item.commentcount = count
                        adapter.notifyItemChanged(dataList.indexOf(item))
                    }
                }
            }
            setupRecyclerView() // Initialize the adapter
        }
    }

    private fun setupRecyclerView() {
        adapter = HomeAdaptor(itemList = dataList,
            currentUserId = null,
            onLikeClick = { item -> toggleLike(item) },
            onComment = { item -> onComment(item) },
            onItemClick = { item -> onItemClick(item) })
        binding.recyclerview.adapter = adapter
    }

    // Comment method
    private fun onComment(modal: HomeModal) {
        val bundle = Bundle().apply {
            putString("postId", modal.id)

        }
        val bottomSheet = CommentsFragment().apply {
            arguments = bundle
        }

        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    //new comment count
    fun updateCommentCount(postId: String, newCount: Int) {
        val post = dataList.find { it.id == postId }
        post?.let {
            it.commentcount = newCount
            adapter.notifyItemChanged(dataList.indexOf(it))
        }
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

        findNavController().navigate(R.id.userProfileFragment, bundle, navOptions)
    }


    private fun toggleLike(item: HomeModal) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val newLikedStatus = !item.isLikedByCurrentUser // Toggle the like status

        firebaseDatabaseManager.toggleLike(item.id!!, currentUserId, newLikedStatus) { success ->
            if (success) {
                // Update local state based on newLikedStatus
                if (newLikedStatus) {
                    item.likedBy = item.likedBy + currentUserId
                    item.likeCount += 1
                } else {
                    item.likedBy = item.likedBy.filter { id -> id != currentUserId }
                    item.likeCount -= 1
                }
                adapter.notifyItemChanged(dataList.indexOf(item)) // Notify adapter of changes
            } else {
                Toast.makeText(context, "Failed to update like", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


