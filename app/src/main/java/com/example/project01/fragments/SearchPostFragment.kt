package com.example.project01.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentSearchPostBinding
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.HomeModal

class SearchPostFragment : Fragment() {

    private lateinit var adaptor: HomeAdaptor
    private lateinit var binding: FragmentSearchPostBinding
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager
    private var dataList = ArrayList<HomeModal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseDatabaseManager = FirebaseDatabaseManager(requireContext())

        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        // Set up search functionality (assuming there's an EditText for search)
    }

    private fun setupRecyclerView() {
        adaptor = HomeAdaptor(
            itemList = dataList,
            currentUserId = null,
            onLikeClick = { item -> (item) },
            onComment = { item -> (item) },
            onItemClick = { item -> (item) }
        )
        binding.recyclerview.adapter = adaptor
    }

   /* override fun search(query: String) {
        if (query.isNotEmpty()) {
            Log.d("Search", "Searching for query: $query")

            firebaseDatabaseManager.searchPosts(query) { results ->
                Log.d("Search", "Received ${results.size} results from Firestore.")

                dataList.clear()
                dataList.addAll(results)

                // Update comment counts for the search results
                dataList.forEach { item ->
                    item.id?.let {
                        firebaseDatabaseManager.getCommentCountForPost(it) { count ->
                            item.commentcount = count
                            adaptor.notifyItemChanged(dataList.indexOf(item))
                        }
                    }
                }
                adaptor.notifyDataSetChanged() // Notify the adapter to refresh the list
            }
        } else {
            Log.d("Search", "Search query is empty, skipping search.")
        }

        setupRecyclerView()
    }*/

}
