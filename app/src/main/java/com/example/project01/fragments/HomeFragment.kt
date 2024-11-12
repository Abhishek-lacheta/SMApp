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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.modal.HomeModal
import com.example.project01.R
import com.example.project01.databinding.FragmentHomeBinding
import com.example.project01.firebase.FirebaseManager
import com.example.project01.firebase.PostFirebaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

class HomeFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentHomeBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var firebaseDatabaseManager: PostFirebaseManager
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var noDataLayout: View
    private var isLoading = false
    private var lastVisibleDocument: DocumentSnapshot? = null

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
        firebaseDatabaseManager = PostFirebaseManager(requireContext())
        firebaseManager = FirebaseManager(requireContext())


        // Setup RecyclerView
        noDataLayout = binding.noDataLayout

        setupRecyclerView()

        // Agar dataList empty hai, toh data fetch karo
        if (dataList.isEmpty()) {
            getPost() // Fetch data if it's empty
        } else {
            // Agar data hai already, toh direct display karo
            noDataLayout.visibility = View.GONE
            binding.recyclerview.visibility = View.VISIBLE
            adapter.addData(dataList) // Existing data ko adapter me add karna
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            getPost()
        }

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Agar list ke end tak pahuch gaye toh data load karo
                if (!recyclerView.canScrollVertically(1)) {
                    // Agar loading nahi ho raha, tabhi new data fetch karo
                    if (!isLoading) {
                        getPost()
                    }
                }
            }
        })
    }

    //fetch home data
    private fun getPost() {
        isLoading = true
        binding.swipeRefreshLayout.isRefreshing = true


        firebaseDatabaseManager.getPost(lastVisibleDocument) { fetchedList, lastVisible ->
            isLoading = false
            // Data load hone ke baad loading ko false karo
            binding.swipeRefreshLayout.isRefreshing = false

            // Agar fetched list empty hai
            if (fetchedList.isEmpty()) {
                if (dataList.isEmpty()) {

                    noDataLayout.visibility = View.VISIBLE
                    binding.recyclerview.visibility = View.GONE
                }
            } else {

                noDataLayout.visibility = View.GONE

                binding.recyclerview.visibility = View.VISIBLE

                lastVisibleDocument = lastVisible
                adapter.addData(fetchedList)

                dataList.addAll(fetchedList)

                adapter.notifyDataSetChanged()
            }
            dataList.forEach { item ->
                item.id?.let {
                    firebaseManager.getCommentCountForPost(it) { count ->
                        item.commentcount = count
                        adapter.notifyItemChanged(dataList.indexOf(item))
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
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
                adapter.notifyItemChanged(dataList.indexOf(item)) // Notify adapter of changes
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