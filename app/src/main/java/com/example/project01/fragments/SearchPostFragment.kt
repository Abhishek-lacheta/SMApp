package com.example.project01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentSearchPostBinding
import com.example.project01.modal.HomeModal
import com.example.project01.viewmodal.SearchViewModel


class SearchPostFragment : Fragment(), Searchable {

    private lateinit var adaptor: HomeAdaptor
    private lateinit var binding: FragmentSearchPostBinding
    private lateinit var searchviewmodel: SearchViewModel
    private var dataList = ArrayList<HomeModal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchviewmodel = ViewModelProvider(this).get(SearchViewModel::class)

        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        // Observe LiveData for post list
        searchviewmodel.searchPost.observe(viewLifecycleOwner, Observer { posts ->
            dataList.clear()
            dataList.addAll(posts)
            setupRecyclerView()
        })
    }
    private fun setupRecyclerView() {
        adaptor = HomeAdaptor(
            itemList = dataList,
            currentUserId = null,
            onLikeClick = { item ->  },
            onComment = { item -> },
            onItemClick = { item ->  },
            openUrl = { link -> }
        )
        binding.recyclerview.adapter = adaptor
    }
    override fun search(query: String) {
        // Trigger search when query is passed
        searchviewmodel.searchPosts(query)
    }
}

