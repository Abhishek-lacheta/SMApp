package com.example.project01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.project01.adaptor.GroupAdapter
import com.example.project01.databinding.FragmentSearchGroupsBinding
import com.example.project01.modal.GroupModal
import com.example.project01.viewmodal.SearchViewModel

class SearchgroupsFragment : Fragment(),Searchable{
    private lateinit var binding: FragmentSearchGroupsBinding
    private lateinit var groupRecyclerAdapter: GroupAdapter
    private lateinit var searchviewmodel: SearchViewModel
    private var dataList = ArrayList<GroupModal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchviewmodel = ViewModelProvider(this).get(SearchViewModel::class)
        binding.groupRecyclerview.layoutManager = GridLayoutManager(context, 2)

        // Observe LiveData for post list
        searchviewmodel.searchGroup.observe(viewLifecycleOwner, Observer { groups ->
            dataList.clear()
            dataList.addAll(groups)
            setupRecyclerView()
        })

    }
    //SetUp RecyclerView
    private fun setupRecyclerView() {
        groupRecyclerAdapter = GroupAdapter(
            itemList = dataList,
            onGroupPopupMenu = { view, item -> },
            onItemClick = { itemList -> (itemList) },
            isPopupMenuVisible = false
        )
        binding.groupRecyclerview.adapter = groupRecyclerAdapter
    }
    override fun search(query: String) {
        searchviewmodel.searchGroup(query)
    }
}
