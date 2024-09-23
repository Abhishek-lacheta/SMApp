package com.example.project01.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.activity.AddHomeActivity
import com.example.project01.adaptor.HomeRecyclerAdaptor
import com.example.project01.modal.HomeRecyclerModal
import com.example.project01.R
import com.example.project01.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private lateinit var adapter: HomeRecyclerAdaptor
    private lateinit var binding: FragmentHomeBinding
    private var dataList = ArrayList<HomeRecyclerModal>()
    private var db = FirebaseFirestore.getInstance()

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

        // Setup Toolbar
        val activity = activity as AppCompatActivity
        val toolbar: Toolbar = binding.hometoolbar
        activity.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        // Setup RecyclerView
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        // Fetch Data from Firestore
        fetchDataFromFirestore()
    }
    private fun fetchDataFromFirestore() {
        db.collection("home").get()
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
        adapter = HomeRecyclerAdaptor(dataList) { item ->
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home_add -> {
                val intent = Intent(requireContext(), AddHomeActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
