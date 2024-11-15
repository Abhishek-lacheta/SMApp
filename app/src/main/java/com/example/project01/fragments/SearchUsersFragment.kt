package com.example.project01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.UserAdaptor
import com.example.project01.databinding.FragmentSearchUsersBinding
import com.example.project01.firebaseold.SearchFirebaseManager
import com.example.project01.modal.HomeModal
import com.example.project01.modal.UserModal
import com.example.project01.viewmodal.SearchViewModel

class SearchUsersFragment : Fragment(), Searchable {
    private lateinit var binding: FragmentSearchUsersBinding
    private lateinit var UserAdaptor: UserAdaptor
    private var dataList = ArrayList<UserModal>()
    private lateinit var searchviewmodel: SearchViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchviewmodel = ViewModelProvider(this).get(SearchViewModel::class)

        binding.userrecyclerview.layoutManager = LinearLayoutManager(context)


        // Observe LiveData for post list
        searchviewmodel.searchUser.observe(viewLifecycleOwner, Observer { users ->
            dataList.clear()
            dataList.addAll(users)
            setupRecyclerView()
        })

    }

    private fun setupRecyclerView() {
        UserAdaptor = UserAdaptor(Users = dataList)
        binding.userrecyclerview.adapter = UserAdaptor
    }

    override fun search(query: String) {
        searchviewmodel.searcUser(query)
    }

}
