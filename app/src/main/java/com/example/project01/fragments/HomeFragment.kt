package com.example.project01.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.activity.AddPostHomeActivity
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.modal.HomeRecyclerModal
import com.example.project01.R
import com.example.project01.databinding.FragmentHomeBinding
import com.example.project01.firebase.FirebaseDatabaseManager


class HomeFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentHomeBinding
    private var dataList = ArrayList<HomeRecyclerModal>()
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseDatabaseManager
        firebaseDatabaseManager = FirebaseDatabaseManager(requireContext())

        // Setup Toolbar
        val activity = activity as AppCompatActivity
        val toolbar = binding.hometoolbar
        activity.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        // Setup RecyclerView
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        // Fetch Data from Firestore
        fetchHomeData()
    }

    private fun fetchHomeData() {
        firebaseDatabaseManager.fetchDataHomeFromFireStore { data ->
            if (data.isEmpty()) return@fetchDataHomeFromFireStore // Handle empty data
            dataList.clear()
            dataList.addAll(data)
            setupRecyclerView() // Initialize the adapter
        }
    }

    private fun setupRecyclerView() {
        adapter = HomeAdaptor(dataList) { item ->
            toggleFavorite(item)
        }
        binding.recyclerview.adapter = adapter
    }

    private fun toggleFavorite(item: HomeRecyclerModal) {
        val newFavoriteStatus = !item.isFavorite
        item.isFavorite = newFavoriteStatus // Update local state

        adapter.notifyDataSetChanged()
        // Update the Firestore document with the new favorite status
        firebaseDatabaseManager.FavoriteStatus(item, newFavoriteStatus)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home_add -> {
                val intent = Intent(requireContext(), AddPostHomeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

