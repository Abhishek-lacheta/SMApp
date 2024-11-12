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
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.R
import com.example.project01.activity.AddPostActivity
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentPostBinding
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseManager
import com.example.project01.firebase.PostFirebaseManager
import com.example.project01.modal.HomeModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

class PostFragment : Fragment() {
    // Group ke andar ki post
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentPostBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var databaseManager: PostFirebaseManager
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var modalId: String
    private lateinit var modalName: String
    private lateinit var userId: String
    private lateinit var navController: NavController
    private val authManager = FirebaseAuthManager()
    private lateinit var noDataLayout: View
    private var isLoading = false
    private var lastVisibleDocument: DocumentSnapshot? = null

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

        noDataLayout = binding.noDataLayout


        setupRecyclerView()

        // Agar dataList empty hai, toh data fetch karo
        if (dataList.isEmpty()) {
            getPosts() // Fetch data if it's empty
        } else {
            // Agar data hai already, toh direct display karo
            noDataLayout.visibility = View.GONE
            binding.recyclerview.visibility = View.VISIBLE
            adapter.addData(dataList) // Existing data ko adapter me add karna
        }


        binding.AddBlocktoolbar.setTitle(modalName)
        binding.AddBlocktoolbar.menu.clear()
        navController = findNavController()
        binding.AddBlocktoolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Agar list ke end tak pahuch gaye toh data load karo
                if (!recyclerView.canScrollVertically(1)) {
                    // Agar loading nahi ho raha, tabhi new data fetch karo
                    if (!isLoading) {
                        getPosts()
                    }
                }
            }
        })

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

    // fetch data on group fragment
    private fun getPosts() {
        isLoading = true

        databaseManager.getPostByGroup(
            userId,
            modalId,
            lastVisibleDocument
        ) { fetchedList, lastVisible ->
            isLoading = false
            if (fetchedList.isEmpty()) {
                if (dataList.isEmpty()) {
                noDataLayout.visibility = View.VISIBLE
                binding.recyclerview.visibility = View.GONE
            } }else {
                noDataLayout.visibility = View.GONE
                binding.recyclerview.visibility = View.VISIBLE

                adapter.addData(fetchedList)
                lastVisibleDocument = lastVisible
                dataList.addAll(fetchedList)

            }
        }
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

    private fun deleteItem(item: HomeModal): Boolean {
        item.id?.let { documentId ->
            databaseManager.deletePost(documentId) { success ->
                if (success) {
                    dataList.remove(item)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                }
            }
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

