package com.example.project01.repositoryfirebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirebaseRepositoryGroup {
    private val database = FirebaseFirestore.getInstance()
    private var authManager = FirebaseAuthManager()
    private val storage = FirebaseStorage.getInstance()

    //AddPostActivity Fetch group name by Id
    fun getGroup(callback: (List<Pair<String, String>>) -> Unit) {
        val nameList = mutableListOf<String>()
        val idList = mutableListOf<String>()
        val currentUser = authManager.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            database.collection("group").whereEqualTo("userId", userId).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        nameList.add(document.getString("name") ?: "")
                        idList.add(document.id)
                    }
                    callback(nameList.zip(idList))
                }
        }
    }
}