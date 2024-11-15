package com.example.project01.repositoryfirebase

import android.net.Uri
import android.util.Log
import com.example.project01.modal.GroupModal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

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

    // AddGroupActivity image upload and save data
    fun saveGroup(imageUri: Uri, name: String, callback: (Boolean) -> Unit) {
        val name_lc = name.lowercase()
        val homeMap = hashMapOf(
            "name" to name,
            "name_lc" to name_lc,
            "userId" to authManager.getCurrentUser()?.uid
        )
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    homeMap["imageUrl"] = downloadUri.toString()
                    saveGroup(homeMap, callback)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    //AddGroupActivity save data in group collection
    private fun saveGroup(homeMap: HashMap<String, String?>, callback: (Boolean) -> Unit) {
        database.collection("group").document().set(homeMap)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Update group collection from AddGroupActivity
    fun updateData(groupId: String, name: String, imageUri: Uri, callback: (Boolean) -> Unit) {
        val name_lc = name.lowercase()
        val groupUpdate = hashMapOf<String, Any>(
            "name" to name,
            "name_lc" to name_lc
        )
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    groupUpdate["imageUrl"] = downloadUri.toString()
                    // Pass groupId along with groupUpdate
                    updateGroup(groupId, groupUpdate, callback)
                }
            }
            .addOnFailureListener {
                callback(false) // Notify failure
            }
    }

    //  update groupData
    private fun updateGroup(
        groupId: String,
        groupUpdate: HashMap<String, Any>,
        callback: (Boolean) -> Unit
    ) {
        // Reference the specific document using the groupId
        database.collection("group").document(groupId)
            .update(groupUpdate)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.w("FirebaseDatabaseManager", "Error updating document", e)
                callback(false)
            }
    }

}