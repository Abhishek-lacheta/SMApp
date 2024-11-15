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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.R
import com.example.project01.activity.AddPostActivity
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentPostBinding
import com.example.project01.repositoryfirebase.FirebaseAuthManager
import com.example.project01.repositoryfirebase.FirebaseManager
import com.example.project01.firebaseold.PostFirebaseManager
import com.example.project01.modal.HomeModal
import com.example.project01.viewmodal.PostViewModel
import com.google.firebase.auth.FirebaseAuth

class PostFragment : Fragment() {
    // Group ke andar ki post
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentPostBinding
    private lateinit var databaseManager: PostFirebaseManager
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var modalId: String
    private lateinit var modalName: String
    private lateinit var userId: String
    private lateinit var navController: NavController
    private val authManager = FirebaseAuthManager()
    private lateinit var noDataLayout: View
    private lateinit var postViewModel: PostViewModel


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get id's from UserFragment
        arguments?.let {
            modalId = it.getString("modalId", "")
            modalName = it.getString("name", "")
            userId = it.getString("userId", "")

        }

        databaseManager = PostFirebaseManager(requireContext())
        firebaseManager= FirebaseManager(requireContext())
        postViewModel=ViewModelProvider(this).get(PostViewModel::class)
        noDataLayout = binding.noDataLayout


        // Observe the deletion status
        postViewModel.isPostDeleted.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
        })


        postViewModel.posts.observe(viewLifecycleOwner, Observer { fetchList->
            if (fetchList.isEmpty()){
                noDataLayout.visibility=View.VISIBLE
                binding.recyclerview.visibility=View.GONE

            }else{
                binding.recyclerview.visibility=View.VISIBLE
                noDataLayout.visibility=View.VISIBLE
                setupRecyclerView(fetchList)
            }

        })
        postViewModel.getPost(userId,modalId)

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
                    // User just liked the post
                    item.likedBy = item.likedBy + currentUserId
                    item.likeCount += 1
                } else {
                    // User just unliked the post
                    item.likedBy = item.likedBy.filter { id -> id != currentUserId }
                    item.likeCount -= 1
                }
                adapter.notifyItemChanged(postViewModel.posts.value?.indexOf(item)?:-1) // Notify adapter of changes
            } else {
                Toast.makeText(context, "Failed to update like", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView(dataList:ArrayList<HomeModal>) {
        val currentUserId = authManager.getCurrentUser()?.uid // Get the current user's UID
        adapter = HomeAdaptor(
            itemList = dataList,
            onShowPopupMenu = { view, item -> showPopupMenu(view, item) },
            currentUserId = currentUserId,
            onLikeClick = { item -> toggleLike(item) },// Pass the like click handler
            onComment = { item -> onComment(item) },
            onItemClick = { item -> onItemClick(item) },
            openUrl = { link -> }
        )
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        binding.recyclerview.adapter = adapter
    }

    fun onItemClick(modal: HomeModal) {
        val bundle = Bundle().apply {
            putString("userId", modal.userId)
        }
        findNavController().navigate(R.id.userProfileFragment, bundle)
    }

    // Modified deleteItem function
    private fun deleteItem(item: HomeModal): Boolean {
        item.id?.let { documentId ->
            postViewModel.deletePost(documentId)  // Call ViewModel to delete the post
            postViewModel.removeFromList(item, postViewModel.posts.value ?: arrayListOf())
            adapter.notifyDataSetChanged()  // Refresh the adapter
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
                    val intent = Intent(requireContext(), AddPostActivity::class.java).apply {
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

