package com.example.project01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.R
import com.example.project01.adaptor.GroupsAdapter


class GroupsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupsAdapter
    private var groups = listOf("Group A", "Group B", "Group C") // Sample data

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = GroupsAdapter(groups)
        recyclerView.adapter = adapter
        return view
    }

    fun filter(query: String) {
        val filteredGroups = groups.filter { it.contains(query, ignoreCase = true) }
        adapter.updateData(filteredGroups)
    }
}
