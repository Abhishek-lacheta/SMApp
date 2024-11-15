package com.example.project01.repositoryfirebase

import com.example.project01.modal.GroupModal
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

    fun searchGroups(searchQuery: String, callback: (List<GroupModal>) -> Unit) {
        val lowerCaseQuery = searchQuery.lowercase()

        val query = database.collection("group")
            .whereGreaterThanOrEqualTo("name_lc", lowerCaseQuery)
            .whereLessThanOrEqualTo("name_lc", lowerCaseQuery + "\uf8ff")
        query.get()
            .addOnSuccessListener { result ->
                val dataList = result.documents.mapNotNull { document ->
                    document.toObject(GroupModal::class.java)
                }
                callback(dataList)
            }

    }

}