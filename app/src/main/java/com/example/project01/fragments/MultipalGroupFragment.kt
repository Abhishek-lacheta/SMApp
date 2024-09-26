package com.example.project01.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.example.project01.modal.HomeRecyclerModal
import com.google.firebase.firestore.FirebaseFirestore


class AddBlockFragment : Fragment() {
private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentAddBloackBinding
    private var dataList = ArrayList<HomeRecyclerModal>()
    private var db = FirebaseFirestore.getInstance()
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
        db.collection("home").whereEqualTo("groupId",modalId).get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                } else {
                    dataList.clear() // Clear existing data
                    for (document in result.documents) {
                        val item = document.toObject(HomeRecyclerModal::class.java)
                        item?.let {
                            it.id = document.id // Set the document ID
                            dataList.add(it)
                        }
                    }
                    setupRecyclerView() // Initialize the adapter
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
