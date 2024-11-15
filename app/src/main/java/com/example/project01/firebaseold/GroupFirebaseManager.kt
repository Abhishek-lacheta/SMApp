package com.example.project01.firebaseold

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.project01.modal.GroupModal
import com.example.project01.repositoryfirebase.FirebaseAuthManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class GroupFirebaseManager(private val context: Context)  {
    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var authManager = FirebaseAuthManager()

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
    fun getGroups(userId: String, callback: (List<GroupModal>) -> Unit) {
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
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error deleting document: ${e.message}", Toast.LENGTH_LONG)
                    .show()
                callback(false)
            }
    }

    // fetch group count on the basis of userId in UserProfileActivity
    fun getGroupCount(userId: String, callback: (Int) -> Unit) {
        database.collection("group").whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val groupCount = result.size()
                callback(groupCount)
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
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                callback(false)
            }
    }

    //AddGroupActivity save data in group collection
    private fun saveGroup(homeMap: HashMap<String, String?>, callback: (Boolean) -> Unit) {
        database.collection("group").document().set(homeMap)
            .addOnSuccessListener {
                Toast.makeText(context, "Successfully Added Data", Toast.LENGTH_SHORT).show()
                callback(true)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add data", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
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

    //Group Fragment fetch group collection is function ka koi use nahi hai example ke liye hai
    fun fetchDataGroupFromeFireStore(callback: (List<GroupModal>) -> Unit) {
        database.collection("group")
            .whereEqualTo("userId", authManager.getCurrentUser()?.uid).get()
            .addOnSuccessListener { result ->
                val dataList = ArrayList<GroupModal>()
                for (document in result.documents) {
                    val item = document.toObject(GroupModal::class.java)
                    item?.id = document.id
                    item?.let { dataList.add(it) }
                }
                callback(dataList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error fetching group data: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                callback(emptyList())
            }
    }

}