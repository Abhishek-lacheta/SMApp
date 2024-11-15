package com.example.project01.firebaseold

import android.content.Context
import android.util.Log
import com.example.project01.modal.GroupModal
import com.example.project01.modal.HomeModal
import com.example.project01.modal.UserModal
import com.google.firebase.firestore.FirebaseFirestore

class SearchFirebaseManager(private val context: Context) {

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
    fun searchUsers(searchQuery: String, callback: (List<UserModal>) -> Unit) {
        val lowerCaseQuery = searchQuery.lowercase()
        Log.d("FirestoreQuery", "Searching for: $lowerCaseQuery")

        val query = database.collection("user")
            .whereGreaterThanOrEqualTo("name_Lc", lowerCaseQuery)
            .whereLessThanOrEqualTo("name_Lc", lowerCaseQuery + "\uf8ff")
        query.get()
            .addOnSuccessListener { result ->
                Log.d("FirestoreQuery", "Query successful, found ${result.size()} results.")

                val dataList = result.documents.mapNotNull { document ->
                    document.toObject(UserModal::class.java)
                }
                callback(dataList)
            }

    }

}