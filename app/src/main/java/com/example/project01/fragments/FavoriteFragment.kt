package com.example.project01.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentFavoriteBinding
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.HomeModal


class FavoriteFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentFavoriteBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var databaseManager: FirebaseDatabaseManager
    private val authManager = FirebaseAuthManager()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseManager = FirebaseDatabaseManager(requireContext())
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        fetchFavoriteData()
    }

    private fun fetchFavoriteData() {
        databaseManager.fetchFavoriteItemsFromFirebase { fetchedList ->
            if (fetchedList.isEmpty()) {
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
            } else {
                dataList.clear()
                dataList.addAll(fetchedList)
                setupRecyclerView()
            }
        }
    }
    private fun toggleFavorite(item: HomeModal) {
        val newFavoriteStatus = !item.isFavorite
        item.isFavorite = newFavoriteStatus // Update local state

        adapter.notifyDataSetChanged()
        // Update the Firestore document with the new favorite status
        databaseManager.FavoriteStatus(item, newFavoriteStatus)
    }


    private fun setupRecyclerView() {
        val currentUserId = authManager.getCurrentUser()?.uid // Get the current user's UID
        adapter = HomeAdaptor(
            itemList = dataList,
            onFavClick = { item -> toggleFavorite(item) },
            currentUserId = currentUserId
        )
        binding.recyclerview.adapter = adapter
    }
}
