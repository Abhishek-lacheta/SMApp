package com.example.project01.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.R
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentFavoriteBinding
import com.example.project01.repositoryfirebase.FirebaseManager
import com.example.project01.modal.HomeModal
import com.example.project01.viewmodal.FavoriteViewModel
import com.google.firebase.auth.FirebaseAuth

class FavoriteFragment : Fragment() {

    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var noDataLayout: View
    private lateinit var firebaseManager: FirebaseManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        firebaseManager = FirebaseManager(requireContext())

        // Set RecyclerView LayoutManager
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        noDataLayout = binding.noDataLayout

        // Observe LiveData from ViewModel
        favoriteViewModel.favoritePosts.observe(viewLifecycleOwner, Observer { fetchedList ->
            // Handle empty data case
            if (fetchedList.isEmpty()) {
                noDataLayout.visibility = View.VISIBLE
                binding.recyclerview.visibility = View.GONE
            } else {
                noDataLayout.visibility = View.GONE
                binding.recyclerview.visibility = View.VISIBLE
                setupRecyclerView(fetchedList)  // Pass the fetched data directly
            }
        })

        // Call getFavoritePosts to fetch data
        favoriteViewModel.getFavoritePosts()
    }
    private fun setupRecyclerView(dataList: List<HomeModal>) {
        adapter = HomeAdaptor(
            itemList = dataList,
            currentUserId = null,
            onLikeClick = { item -> toggleLike(item) },
            onComment = { item -> onComment(item) },
            onItemClick = { item -> onItemClick(item) },
            openUrl = { link -> }
        )
        binding.recyclerview.adapter = adapter
    }

    private fun onComment(modal: HomeModal) {
        val bundle = Bundle().apply {
            putString("postId", modal.id)
        }
        val bottomSheet = CommentsFragment().apply {
            arguments = bundle
        }
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun toggleLike(item: HomeModal) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val newLikedStatus = !item.isLikedByCurrentUser // Toggle the like status

        firebaseManager.toggleLike(item.id!!, currentUserId, newLikedStatus) { success ->
            if (success) {
                // Update local state based on newLikedStatus
                if (newLikedStatus) {
                    item.likedBy = item.likedBy + currentUserId
                    item.likeCount += 1
                } else {
                    item.likedBy = item.likedBy.filter { id -> id != currentUserId }
                    item.likeCount -= 1
                }
                adapter.notifyItemChanged(favoriteViewModel.favoritePosts.value?.indexOf(item) ?: -1)
            } else {
                Toast.makeText(context, "Failed to update like", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateCommentCount(postId: String, newCount: Int) {
        // Find the post in the list and update its comment count
        val post = favoriteViewModel.favoritePosts.value?.find { it.id == postId }
        post?.let {
            it.commentcount = newCount
            adapter.notifyItemChanged(favoriteViewModel.favoritePosts.value?.indexOf(it) ?: -1)
        }
    }
    fun onItemClick(modal: HomeModal) {
        val bundle = Bundle().apply {
            putString("userId", modal.userId)
        }
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
        findNavController().navigate(R.id.userProfileFragment, bundle, navOptions)
    }
}


