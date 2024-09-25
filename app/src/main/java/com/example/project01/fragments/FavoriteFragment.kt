package com.example.project01.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.adaptor.HomeRecyclerAdaptor
import com.example.project01.databinding.FragmentFavoriteBinding
import com.example.project01.modal.HomeRecyclerModal
import com.google.firebase.firestore.FirebaseFirestore


class FavoriteFragment : Fragment() {
    private lateinit var adapter: HomeRecyclerAdaptor
    private lateinit var binding: FragmentFavoriteBinding
    private var dataList = ArrayList<HomeRecyclerModal>()
    private var db = FirebaseFirestore.getInstance()
    private val favrite: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        fetchdatafromFirestore()

    }

    private fun fetchdatafromFirestore() {
        db.collection("home")
            .whereEqualTo("isFavorite", favrite) // Filter by modal ID
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                } else {
                    dataList.clear() // Clear existing data
                    for (document in result.documents) {
                        val item = document.toObject(HomeRecyclerModal::class.java)
                        item?.let {
                            it.id = document.id
                            dataList.add(it)
                        }
                    }

                    setupRecyclerView()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error fetching data: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

    }

    private fun setupRecyclerView() {
        adapter = HomeRecyclerAdaptor(dataList) { item ->
            toggleFavorite(item)
        }
        binding.recyclerview.adapter = adapter
    }

    // toggleFavorite favorite ke click pr adaptor se chl rhe hai
    private fun toggleFavorite(item: HomeRecyclerModal) {
        val newFavoriteStatus = !item.isFavorite
        item.isFavorite = newFavoriteStatus // Update local state
        adapter.notifyDataSetChanged()
        // Update the Firestore document with the new favorite status
        item.id?.let {
            db.collection("home").document(it).update("isFavorite", newFavoriteStatus)
                .addOnSuccessListener {
                    Log.d("FavoriteStatus", "Favorite status updated successfully.")
                }
                .addOnFailureListener { e ->
                    Log.e("FavoriteStatus", "Error updating favorite status", e)
                }
        }
    }
}