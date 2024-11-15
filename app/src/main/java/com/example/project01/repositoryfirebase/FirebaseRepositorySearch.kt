package com.example.project01.repositoryfirebase

import com.example.project01.modal.HomeModal
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepositorySearch() {
    private val database = FirebaseFirestore.getInstance()

    fun searchPosts(searchQuery: String, callback: (List<HomeModal>) -> Unit) {
        val lowerCaseQuery = searchQuery.lowercase()

        val query = database.collection("home")
            .whereGreaterThanOrEqualTo("title_lc", lowerCaseQuery)
            .whereLessThanOrEqualTo("title_lc", lowerCaseQuery + "\uf8ff")
        query.get()
            .addOnSuccessListener { result ->
                val dataList = result.documents.mapNotNull { document ->
                    document.toObject(HomeModal::class.java)
                }
                callback(dataList)
            }

    }
}