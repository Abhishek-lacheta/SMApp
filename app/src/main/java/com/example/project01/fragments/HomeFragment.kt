package com.example.project01.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.activity.AddPostHomeActivity
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.modal.HomeModal
import com.example.project01.R
import com.example.project01.databinding.FragmentHomeBinding
import com.example.project01.dialogs.BottomSeatFragment
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseDatabaseManager
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentHomeBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager
    private val authManager = FirebaseAuthManager()


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseDatabaseManager
        firebaseDatabaseManager = FirebaseDatabaseManager(requireContext())

        // Setup Toolbar
        val activity = activity as AppCompatActivity
        val toolbar = binding.hometoolbar
        activity.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        // Setup RecyclerView
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        // Fetch Data from Firestore
        fetchHomeData()
    }

    //comment ke liye
    fun Oncomment(modal: HomeModal) {

        val bundle = Bundle().apply {

            putString("postId", modal.id)
        }
        val bottomSheet = BottomSeatFragment().apply {
            arguments = bundle
        }

        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun fetchHomeData() {
        firebaseDatabaseManager.fetchDataHomeFromFireStore { data ->
            if (data.isEmpty()) return@fetchDataHomeFromFireStore // Handle empty data
            dataList.clear()
            dataList.addAll(data)
            setupRecyclerView() // Initialize the adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home_add -> {
                val intent = Intent(requireContext(), AddPostHomeActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleLike(item: HomeModal) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val newLikedStatus = !item.isLikedByCurrentUser // Toggle the like status

        firebaseDatabaseManager.toggleLike(item.id!!, currentUserId, newLikedStatus) { success ->
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
            currentUserId = currentUserId,
            onLikeClick = { item -> toggleLike(item) },// like click handler
            Oncomment = { item -> Oncomment(item) }
        )
        binding.recyclerview.adapter = adapter
    }

}