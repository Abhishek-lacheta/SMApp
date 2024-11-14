package com.example.project01.repositoryfirebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.project01.modal.HomeModal
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepositoryPost {

    private val firestore = FirebaseFirestore.getInstance()
    // Method to fetch favorite posts from Firebase Firestore

    fun getFavoritePosts(callback: (List<HomeModal>) -> Unit) {
        val database = FirebaseFirestore.getInstance()

        // Fetch data from Firestore
        database.collection("home")
            .whereEqualTo("likedByCurrentUser", true)
            .get()
            .addOnSuccessListener { result ->
                val fetchedList = ArrayList<HomeModal>()
                for (document in result.documents) {
                    val item = document.toObject(HomeModal::class.java)
                    item?.id = document.id
                    item?.let { fetchedList.add(it) }
                }

                // Return the list via callback
                callback(fetchedList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRepositoryPost", "Error fetching data: ${exception.message}")
                callback(emptyList())  // Return empty list in case of error
            }
    }

    fun getPosts(callback: (List<HomeModal>) -> Unit) {
        val database = FirebaseFirestore.getInstance()

        // Fetch data from Firestore
        database.collection("home")
            .get()
            .addOnSuccessListener { result ->
                val fetchedList = ArrayList<HomeModal>()
                for (document in result.documents) {
                    val item = document.toObject(HomeModal::class.java)
                    item?.id = document.id
                    item?.let { fetchedList.add(it) }
                }

                // Return the list via callback
                callback(fetchedList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRepositoryPost", "Error fetching data: ${exception.message}")
                callback(emptyList())  // Return empty list in case of error
            }
    }


}