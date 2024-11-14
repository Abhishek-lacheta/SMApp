package com.example.project01.repositoryfirebase

import android.net.Uri
import android.util.Log
import com.example.project01.modal.HomeModal
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepositoryPost() {

    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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
}