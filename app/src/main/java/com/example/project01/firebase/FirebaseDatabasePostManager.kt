package com.example.project01.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.project01.modal.HomeModal
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseDatabasePostManager(private val context: Context) {
    private val database = FirebaseFirestore.getInstance()

    //Home fragment fetch home collection
    fun getPost(
        lastVisible: DocumentSnapshot? = null,
        callback: (List<HomeModal>, DocumentSnapshot?) -> Unit
    ) {
        var query = database.collection("home").limit(2)


        lastVisible?.let {
            query = query.startAfter(it)
        }

        query.get()
            .addOnSuccessListener { result ->
                val dataList = ArrayList<HomeModal>()
                var lastVisibleDoc: DocumentSnapshot? = null

                for (document in result.documents) {
                    val item = document.toObject(HomeModal::class.java)
                    item?.let {
                        it.id = document.id
                        dataList.add(it)
                    }
                }

                // Get the last document for pagination
                if (result.size() > 0) {
                    lastVisibleDoc = result.documents[result.size() - 1]
                }

                // Return data and the last document to the fragment
                callback(dataList, lastVisibleDoc)
            }
    }


    // Post Fragment Group ke andar ki post fetch data groupById
    fun getPostByGroup(
        userId: String,
        groupId: String,
        lastVisible: DocumentSnapshot? = null,
        callback: (List<HomeModal>, DocumentSnapshot?) -> Unit
    ) {
        var query = database.collection("home")
            .whereEqualTo("userId", userId)
            .whereEqualTo("groupId", groupId).limit(2)

        lastVisible?.let {
            query = query.startAfter(it)
        }
        query.get().addOnSuccessListener { result ->
            val dataList = ArrayList<HomeModal>()
            var lastVisibleDoc: DocumentSnapshot? = null

            for (document in result.documents) {
                val item = document.toObject(HomeModal::class.java)
                item?.let {
                    it.id = document.id
                    dataList.add(it)
                }
            }

            // Get the last document for pagination
            if (result.size() > 0) {
                lastVisibleDoc = result.documents[result.size() - 1]
            }

            // Return data and the last document to the fragment
            callback(dataList, lastVisibleDoc)
        }
    }

    // PostFragment Delete a document
    fun deletePost(documentId: String, callback: (Boolean) -> Unit) {
        database.collection("home")
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

    // Favorite Fragment fetch home data
    fun getFavoritePost(callback: (List<HomeModal>) -> Unit) {
        database.collection("home")
            .whereEqualTo("likedByCurrentUser", true)
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
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error fetching favorite data: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                callback(emptyList())
            }
    }
    // Toggle like status like funcnality
    fun toggleLike(postId: String, userId: String, isLiked: Boolean, callback: (Boolean) -> Unit) {
        val postRef = database.collection("home").document(postId)

        postRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val homeModal = document.toObject(HomeModal::class.java)
                homeModal?.let {
                    if (isLiked) {
                        // Add user to likedBy list and increment likeCount
                        it.likedBy = it.likedBy + userId
                        it.likeCount += 1
                    } else {
                        // Remove user from likedBy list and decrement likeCount
                        it.likedBy = it.likedBy.filter { id -> id != userId }
                        it.likeCount -= 1
                    }
                    // Update the Firestore document
                    postRef.set(it).addOnSuccessListener {
                        callback(true)
                    }.addOnFailureListener { e ->
                        Log.e("LikePost", "Error updating like status", e)
                        callback(false)
                    }
                }
            }
        }.addOnFailureListener { e ->
            Log.e("LikePost", "Error fetching post", e)
            callback(false)
        }
    }

    //Get Comment Count
    fun getCommentCountForPost(postId: String, callback: (Int) -> Unit) {
        database.collection("home").document(postId).collection("comments")
            .get()
            .addOnSuccessListener { querySnapshot ->
                callback(querySnapshot.size())
            }
            .addOnFailureListener { e ->
                Log.e("GetCommentCount", "Error getting comments", e)
                callback(0)
            }
    }


}