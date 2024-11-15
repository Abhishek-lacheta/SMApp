package com.example.project01.repositoryfirebase

import android.content.Context
import android.util.Log
import com.example.project01.modal.HomeModal
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseManager(private val context: Context) {
    private val database = FirebaseFirestore.getInstance()

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