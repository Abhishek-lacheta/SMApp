package com.example.project01.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentAddBloackBinding
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.HomeRecyclerModal


class AddBlockFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentAddBloackBinding
    private var dataList = ArrayList<HomeRecyclerModal>()
    private lateinit var databaseManager: FirebaseDatabaseManager
    private lateinit var modalId: String
    private lateinit var modalName: String
    private lateinit var navController: NavController

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBloackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            modalId = it.getString("modalId", "")
            modalName = it.getString("name", "")
        }

        databaseManager = FirebaseDatabaseManager(requireContext())
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        // Fetch Data from Firestore
        fetchDataFromFirestore()
        binding.AddBlocktoolbar.setTitle(modalName)
        binding.AddBlocktoolbar.menu.clear()
        navController = findNavController()
        binding.AddBlocktoolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
    }

    private fun fetchDataFromFirestore() {
        databaseManager.fetchDataByGroupId(modalId) { fetchedList ->
            if (fetchedList.isEmpty()) {
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
            } else {
                dataList.clear() // Clear existing data
                dataList.addAll(fetchedList)
                setupRecyclerView() // Initialize the adapter
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = HomeAdaptor(dataList) { item -> toggleFavorite(item) }
        binding.recyclerview.adapter = adapter
    }

    private fun toggleFavorite(item: HomeRecyclerModal) {
        val newFavoriteStatus = !item.isFavorite
        item.isFavorite = newFavoriteStatus // Update local state

        adapter.notifyDataSetChanged()
        // Update the Firestore document with the new favorite status
        databaseManager.FavoriteStatus(item, newFavoriteStatus)
    }
}

