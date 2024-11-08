package com.example.project01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.UserAdaptor
import com.example.project01.databinding.FragmentSearchUsersBinding
import com.example.project01.firebase.SearchFirebaseManager
import com.example.project01.modal.UserModal

class SearchUsersFragment : Fragment(), Searchable {
    private lateinit var binding: FragmentSearchUsersBinding
    private val itemList = mutableListOf<UserModal>()
    private lateinit var UserAdaptor: UserAdaptor
    private lateinit var databaseManager:SearchFirebaseManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseManager = SearchFirebaseManager(requireContext())
        binding.userrecyclerview.layoutManager = LinearLayoutManager(context)


    }

    private fun setupRecyclerView() {
        UserAdaptor = UserAdaptor(Users = itemList)
        binding.userrecyclerview.adapter = UserAdaptor
    }

    override fun search(query: String) {
        if (query.isNotEmpty()) {

            databaseManager.searchUsers(query) { fetchedList ->
                itemList.clear()
                itemList.addAll(fetchedList)
                UserAdaptor.notifyDataSetChanged()
            }

        }

        setupRecyclerView()
    }

}
