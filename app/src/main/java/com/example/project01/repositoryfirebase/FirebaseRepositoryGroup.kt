package com.example.project01.repositoryfirebase

import com.example.project01.modal.GroupModal
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
    // fetch group data on the basis of userId in UserProfileFragment and UserFragment
    fun getGroups(userId: String, callback: (ArrayList<GroupModal>) -> Unit) {
        database.collection("group").whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val dataList = ArrayList<GroupModal>()
                for (document in result.documents) {
                    val item = document.toObject(GroupModal::class.java)
                    item?.id = document.id
                    item?.let { dataList.add(it) }
                }
                callback(dataList)
            }
    }

    //UserFragment delele dacument
    fun deleteGroup(documentId: String, callback: (Boolean) -> Unit) {
        database.collection("group")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)  // If failed, callback with false
            }

    }

}