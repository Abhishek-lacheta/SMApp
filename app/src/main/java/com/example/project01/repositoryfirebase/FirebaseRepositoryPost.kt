package com.example.project01.repositoryfirebase

import android.net.Uri
import android.util.Log
import com.example.project01.modal.HomeModal
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
class FirebaseRepositoryPost() {
    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var authManager = FirebaseAuthManager()

    //Fetch Favorite Post Frome FavoriteFragment
    fun getFavoritePosts(callback: (List<HomeModal>) -> Unit) {
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

                callback(fetchedList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRepositoryPost", "Error fetching data: ${exception.message}")
                callback(emptyList())  // Return empty list in case of error
            }
    }

    //Fetch Post From HomeFragment
    fun getPosts(callback: (List<HomeModal>) -> Unit) {
        database.collection("home")
            .get()
            .addOnSuccessListener { result ->
                val fetchedList = ArrayList<HomeModal>()
                for (document in result.documents) {
                    val item = document.toObject(HomeModal::class.java)
                    item?.id = document.id
                    item?.let { fetchedList.add(it) }
                }

                callback(fetchedList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRepositoryPost", "Error fetching data: ${exception.message}")
                callback(emptyList())  // Return empty list in case of error
            }
    }

    // PostFragment Group ke andar ki post fetch data groupById
    fun getPostByGroup(
        userId: String,
        groupId: String,
        callback: (ArrayList<HomeModal>) -> Unit
    ) {
        database.collection("home")
            .whereEqualTo("userId", userId)
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener { result ->
                val dataList = ArrayList<HomeModal>()
                for (document in result.documents) {
                    val item = document.toObject(HomeModal::class.java)
                    item?.id = document.id
                    item?.let { dataList.add(it) }
                }
                callback(dataList)
            }
    }

    // Delete post method From PostFragment
    fun deletePost(documentId: String, callback: (Boolean) -> Unit) {
        database.collection("home")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                callback(true)  // If successful, callback with true
            }
            .addOnFailureListener {
                callback(false)  // If failed, callback with false
            }
    }

    // Save data to Firestore and Storage Frome AddPostActivity
    suspend fun savePost(
        imageUri: Uri,
        title: String,
        desc: String,
        selectedGroupId: String?,
        isFavorite: Boolean,
        linkAddress: String,
        callback: (Boolean) -> Unit
    ) {
        val currentUser = authManager.getCurrentUser()
        currentUser?.let { user1 ->
            try {
                // User data fetch karo Firestore se
                val user = database.collection("user").document(user1.uid).get().await()
                val userName = user.getString("name") ?: "Unknown User"
                val image = user.getString("profileImageUrl")

                // Home data prepare karo
                val homeMap = hashMapOf(
                    "title" to title,
                    "title_lc" to title.lowercase(),
                    "desc" to desc,
                    "created_at" to FieldValue.serverTimestamp(),
                    "groupId" to (selectedGroupId ?: ""),
                    "isFavorite" to isFavorite,
                    "userId" to user1.uid,
                    "userName" to userName,
                    "image" to image,
                    "linkAddress" to linkAddress
                )

                // Image ko Firebase Storage pe upload karo
                val storageRef = storage.reference
                val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
                imageRef.putFile(imageUri).await() // await for suspending functions

                // Image ka download URL lo
                val downloadUri = imageRef.downloadUrl.await()

                // Image URL ko home data mein add karo
                homeMap["imageUrl"] = downloadUri.toString()

                // Firestore mein data save karo
                database.collection("home").document().set(homeMap).await()

                // Success callback
                callback(true)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            }
        }
    }

    // Function to update existing post From AddPostActivity
    suspend fun updateData(
        postId: String,
        imageUri: Uri,
        title: String,
        description: String,
        callback: (Boolean) -> Unit
    ) {
        try {
            val title_lc = title.lowercase()
            val postUpdates = hashMapOf<String, Any>(
                "title" to title,
                "title_lc" to title_lc,
                "desc" to description
            )

            val imageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
            imageRef.putFile(imageUri).await()  // Upload image to Firebase Storage

            val downloadUri = imageRef.downloadUrl.await()  // Get the download URL
            postUpdates["imageUrl"] = downloadUri.toString()

            updatePost(postId, postUpdates, callback)  // Update Firestore document
        } catch (e: Exception) {
            e.printStackTrace()
            callback(false)  // Failure
        }
    }

    // Helper function to update Firestore document
    private suspend fun updatePost(
        postId: String,
        postUpdates: HashMap<String, Any>,
        callback: (Boolean) -> Unit
    ) {
        try {
            database.collection("home")
                .document(postId)
                .update(postUpdates).await()

            callback(true)  // Success
        } catch (e: Exception) {
            e.printStackTrace()
            callback(false)  // Failure
        }
    }

}