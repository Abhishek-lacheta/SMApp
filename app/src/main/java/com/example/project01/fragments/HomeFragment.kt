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
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.modal.HomeModal
import com.example.project01.R
import com.example.project01.databinding.FragmentHomeBinding
import com.example.project01.firebase.FirebaseDatabasePostManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

class HomeFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentHomeBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var firebaseDatabaseManager: FirebaseDatabasePostManager
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
        firebaseDatabaseManager = FirebaseDatabasePostManager(requireContext())


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
        isLoading = true // Data load ho raha hai, loading flag set karo

        // Firestore se data fetch karo
        firebaseDatabaseManager.getPost(lastVisibleDocument) { fetchedList, lastVisible ->
            isLoading = false  // Data load hone ke baad loading ko false karo

            // Agar fetched list empty hai
            if (fetchedList.isEmpty()) {
                if (dataList.isEmpty()) {
                    // Agar dono dataList aur fetchedList empty hai, "No Data Found" show karo
                    noDataLayout.visibility = View.VISIBLE
                    binding.recyclerview.visibility = View.GONE
                }
            } else {
                // Agar data mil gaya hai, "No Data Found" ko hide karo
                noDataLayout.visibility = View.GONE
                binding.recyclerview.visibility = View.VISIBLE

                // Naye data ko existing list me add karo
                adapter.addData(fetchedList)

                // Pagination ke liye lastVisibleDocument update karo
                lastVisibleDocument = lastVisible

                // Data ko add karo dataList me
                dataList.addAll(fetchedList)
            }
            dataList.forEach{item->
                item.id?.let {
                    firebaseDatabaseManager.getCommentCountForPost(it){
                        count->
                        item.commentcount=count
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
            onItemClick = { item -> onItemClick(item) }
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
        val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

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



