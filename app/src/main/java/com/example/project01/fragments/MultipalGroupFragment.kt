package com.example.project01.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project01.R
import com.example.project01.activity.AddPostHomeActivity
import com.example.project01.adaptor.HomeAdaptor
import com.example.project01.databinding.FragmentMultipalGroupBinding
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.HomeModal

class AddBlockFragment : Fragment() {
    private lateinit var adapter: HomeAdaptor
    private lateinit var binding: FragmentMultipalGroupBinding
    private var dataList = ArrayList<HomeModal>()
    private lateinit var databaseManager: FirebaseDatabaseManager
    private lateinit var modalId: String
    private lateinit var modalName: String
    private lateinit var navController: NavController
    private val authManager = FirebaseAuthManager()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMultipalGroupBinding.inflate(inflater, container, false)
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
            onShowPopupMenu = { view, item -> showPopupMenu(view, item) },
            currentUserId = currentUserId
        )
        binding.recyclerview.adapter = adapter
    }

    private fun deleteItem(item: HomeModal): Boolean {
        item.id?.let { documentId ->
            databaseManager.deleteData(documentId) { success ->
                if (success) {
                    dataList.remove(item)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(context, "Item ID is null", Toast.LENGTH_SHORT).show()
        }

        return true
    }
    private fun showPopupMenu(view: View, item: HomeModal) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.confirm_delete -> deleteItem(item)
                R.id.update -> {
                    val intent = Intent(requireContext(), AddPostHomeActivity::class.java).apply {
                        putExtra("id",item.id)
                        putExtra("itemtitle",item.title)
                        putExtra("itemdes",item.desc)
                    }
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }


}

