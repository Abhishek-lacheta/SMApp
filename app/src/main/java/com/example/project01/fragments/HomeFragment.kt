package com.example.project01.fragments
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.modal.HomeModal
import com.example.project01.R
import com.example.project01.databinding.FragmentHomeBinding
import com.example.project01.repositoryfirebase.FirebaseManager
import com.example.project01.repositoryfirebase.PostFirebaseManager
import com.example.project01.viewmodal.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var firebaseDatabaseManager: PostFirebaseManager
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var noDataLayout: View
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

        homeViewModel=ViewModelProvider(this).get(HomeViewModel::class)
        firebaseDatabaseManager = PostFirebaseManager(requireContext())
        firebaseManager = FirebaseManager(requireContext())
        // Setup RecyclerView
        noDataLayout = binding.noDataLayout

        homeViewModel.Posts.observe(viewLifecycleOwner, Observer { fetchedList ->
            if (fetchedList.isEmpty()) {
                noDataLayout.visibility = View.VISIBLE
                binding.recyclerview.visibility = View.GONE
            } else {
                noDataLayout.visibility = View.GONE
                binding.recyclerview.visibility = View.VISIBLE
                setupRecyclerView(fetchedList)
            }
            homeViewModel.Posts.value?.forEach { item ->
                item.id?.let {
                    firebaseManager.getCommentCountForPost(it) { count ->
                        item.commentcount = count
                        adapter.notifyItemChanged(homeViewModel.Posts.value?.indexOf(item)?: -1)
                    }
                }
            }

        })
        homeViewModel.getPosts()

        /*binding.swipeRefreshLayout.setOnRefreshListener {

        }*/


    }
    private fun setupRecyclerView(dataList: List<HomeModal>) {
        adapter = HomeAdaptor(
            itemList = dataList,
            currentUserId = null,
            onLikeClick = { item -> toggleLike(item) },
            onComment = { item -> onComment(item) },
            onItemClick = { item -> onItemClick(item) },
            openUrl = { link -> openUrl(link) }
        )
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
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
        //tag =Get the tag name of the fragment, if specified.
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    //new comment count
    fun getCommentCount(postId: String, newCount: Int) {
        val post = homeViewModel.Posts.value?.find{ it.id == postId }
        post?.let {
            it.commentcount = newCount
            adapter.notifyItemChanged(homeViewModel.Posts.value?.indexOf(it)?: -1)
        }
    }

    fun onItemClick(modal: HomeModal) {
        val bundle = Bundle().apply {
            putString("userId", modal.userId)
        }
        val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        findNavController().navigate(R.id.userProfileFragment, bundle, navOptions)
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
                adapter.notifyItemChanged(homeViewModel.Posts.value?.indexOf(item)?: -1)
            } else {
                Toast.makeText(context, "Failed to update like", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openUrl(url: String) {
        if (url.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No link available", Toast.LENGTH_SHORT).show()
            return
        }

        // Ensure URL starts with "http://" or "https://"
        val formattedUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "https://$url"
        }
        try {
            // Create CustomTabsIntent
            val customTabsIntent = CustomTabsIntent.Builder().build()

            // Launch URL in Chrome Custom Tab
            customTabsIntent.launchUrl(requireContext(), Uri.parse(formattedUrl))

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error opening link", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}