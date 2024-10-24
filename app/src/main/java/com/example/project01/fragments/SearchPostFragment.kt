package com.example.project01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.R
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
        fetchHomeData()
    }

    private fun setupRecyclerView() {
        adaptor = HomeAdaptor(itemList = dataList,
            currentUserId = null,
            onLikeClick = { item -> (item) },
            onComment = { item -> (item) },
            onItemClick = { item -> (item) })
        binding.recyclerview.adapter = adaptor
    }

    private fun fetchHomeData() {
        firebaseDatabaseManager.fetchDataHomeFromFireStore { fetchedList ->
            if (fetchedList.isEmpty()) {

            } else {
                dataList.clear()
                dataList.addAll(fetchedList)
            }

            // Fetch comment counts for each post
            dataList.forEach { item ->
                item.id?.let {
                    firebaseDatabaseManager.getCommentCountForPost(it) { count ->
                        item.commentcount = count
                        adaptor.notifyItemChanged(dataList.indexOf(item))
                    }
                }
            }
            setupRecyclerView() // Initialize the adapter
        }
    }

}