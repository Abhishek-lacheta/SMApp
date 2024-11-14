package com.example.project01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.project01.adaptor.GroupAdapter
import com.example.project01.databinding.FragmentSearchGroupsBinding
import com.example.project01.repositoryfirebase.FirebaseAuthManager
import com.example.project01.repositoryfirebase.SearchFirebaseManager
import com.example.project01.modal.GroupModal


class SearchgroupsFragment : Fragment(),Searchable{
    private lateinit var binding: FragmentSearchGroupsBinding
    private lateinit var groupRecyclerAdapter: GroupAdapter
    private lateinit var databaseManager: SearchFirebaseManager
    private var itemList = mutableListOf<GroupModal>()
    private var authManager = FirebaseAuthManager()
    val currentUser = authManager.getCurrentUser()
    val userId = currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseManager = SearchFirebaseManager(requireContext())
        binding.groupRecyclerview.layoutManager = GridLayoutManager(context, 2)

    }

    //SetUp RecyclerView
    private fun setupRecyclerView() {
        groupRecyclerAdapter = GroupAdapter(
            itemList = itemList,
            onGroupPopupMenu = { view, item -> },
            onItemClick = { itemList -> (itemList) },
            isPopupMenuVisible = false
        )
        binding.groupRecyclerview.adapter = groupRecyclerAdapter
    }


    override fun search(query: String) {
        if (query.isNotEmpty()) {

            databaseManager.searchGroups(query) { fetchedList ->
                itemList.clear()
                itemList.addAll(fetchedList)
                groupRecyclerAdapter.notifyDataSetChanged()
            }

        }
        setupRecyclerView()
    }
}
