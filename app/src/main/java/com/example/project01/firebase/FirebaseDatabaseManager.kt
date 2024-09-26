package com.example.project01.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.project01.modal.GroupRecyclerModal
import com.example.project01.modal.HomeRecyclerModal
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class FirebaseDatabaseManager(private val context: Context) {
    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    //Home fragment fetch home collection
    fun fetchDataHomeFromFireStore(callback: (List<HomeRecyclerModal>) -> Unit) {
        database.collection("home").get()
            .addOnSuccessListener { result ->
                val dataList = ArrayList<HomeRecyclerModal>()
                if (result.isEmpty) {
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                    callback(dataList)
                } else {
                    for (document in result.documents) {
                        val item = document.toObject(HomeRecyclerModal::class.java)
                        item?.let {
                            it.id = document.id // Set the document ID
                            dataList.add(it)
                        }
                    }
                    callback(dataList)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error fetching data: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                callback(emptyList())
            }
    }

    // Update result from fragment this is commen function
    fun FavoriteStatus(item: HomeRecyclerModal, newFavoriteStatus: Boolean) {
        item.id?.let {
            database.collection("home").document(it).update("isFavorite", newFavoriteStatus)
                .addOnSuccessListener {
                    Log.d("FavoriteStatus", "Favorite status updated successfully.")
                }
                .addOnFailureListener { e ->
                    Log.e("FavoriteStatus", "Error updating favorite status", e)
                }
        }
    }

    //Group Fragment fetch group collection
    fun fetchDataGroupFromeFireStore(callback: (List<GroupRecyclerModal>) -> Unit) {
        database.collection("group").get()
            .addOnSuccessListener { result ->
                val dataList = ArrayList<GroupRecyclerModal>()
                for (document in result.documents) {
                    val item = document.toObject(GroupRecyclerModal::class.java)
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


    // favrate Fragment fetch home data
    fun fetchFavoriteItemsFromFirebase(callback: (List<HomeRecyclerModal>) -> Unit) {
        database.collection("home")
            .whereEqualTo("isFavorite", true) // Filter for favorites
            .get()
            .addOnSuccessListener { result ->
                val dataList = ArrayList<HomeRecyclerModal>()
                for (document in result.documents) {
                    val item = document.toObject(HomeRecyclerModal::class.java)
                    item?.id = document.id
                    item?.let { dataList.add(it) }
                }
                callback(dataList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error fetching favorite data: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                callback(emptyList())
            }
    }

    //MultipalGroupFragment fetch data groupById
    fun fetchDataByGroupId(groupId: String, callback: (List<HomeRecyclerModal>) -> Unit) {
        database.collection("home")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { result ->
                val dataList = ArrayList<HomeRecyclerModal>()
                for (document in result.documents) {
                    val item = document.toObject(HomeRecyclerModal::class.java)
                    item?.id = document.id
                    item?.let { dataList.add(it) }
                }
                callback(dataList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error fetching data: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                callback(emptyList())
            }
    }

    // AddPostGroupActivity image upload and save data
    fun uploadImageAndSaveData(imageUri: Uri, name: String, callback: (Boolean) -> Unit) {
        val homeMap = hashMapOf("name" to name)
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    homeMap["imageUrl"] = downloadUri.toString()
                    saveGroupData(homeMap, callback)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                callback(false)
            }
    }

    //AddPostGroup Activity save data in group collection
    private fun saveGroupData(homeMap: HashMap<String, String>, callback: (Boolean) -> Unit) {
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

    //AddPostHomeActivity Fetch group data
    fun fetchGroupData(callback: (List<Pair<String, String>>) -> Unit) {
        val nameList = mutableListOf<String>()
        val idList = mutableListOf<String>()

        database.collection("group").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    nameList.add(document.getString("name") ?: "")
                    idList.add(document.id)
                }
                callback(nameList.zip(idList)) // Combine names and IDs into pairs
            }
            .addOnFailureListener { exception ->
                Log.w("FirebaseDB", "Error getting documents: ", exception)
                callback(emptyList()) // Return empty list on failure
            }
    }

    // AddPostHomeActivity Upload image and save home data
    fun uploadImageAndSaveData(
        imageUri: Uri,
        title: String,
        desc: String,
        selectedGroupId: String?,
        isFavorite: Boolean,
        callback: (Boolean) -> Unit
    ) {
        val homeMap = hashMapOf(
            "title" to title,
            "desc" to desc,
            "created_at" to FieldValue.serverTimestamp(),
            "groupId" to (selectedGroupId ?: ""), // Add groupId to the homeMap
            "isFavorite" to isFavorite // Add favorite status to homeMap,
        )
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    homeMap["imageUrl"] = downloadUri.toString()
                    saveHomeData(homeMap, callback)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                callback(false) // Notify failure
            }
    }

    //AddPostHomeActivity save data
    private fun saveHomeData(homeMap: HashMap<String, Any>, callback: (Boolean) -> Unit) {
        database.collection("home").document().set(homeMap)
            .addOnSuccessListener {
                Toast.makeText(context, "Successfully Added Data", Toast.LENGTH_SHORT).show()
                callback(true) // Notify success
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add data", Toast.LENGTH_SHORT).show()
                callback(false) // Notify failure
            }
    }
}









