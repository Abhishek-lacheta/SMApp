package com.example.project01.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.project01.modal.GroupModal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class FirebaseDatabaseManager(private val context: Context) {
    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var authManager = FirebaseAuthManager()








    //EditProfileActivity
    fun getUserData(onDataLoaded: (username: String?, email: String?, imageUrl: String?) -> Unit) {
        val currentUser = authManager.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = database.collection("user").document(userId)

            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("name")
                    val email = document.getString("email")
                    val imageUrl = document.getString("profileImageUrl")
                    onDataLoaded(username, email, imageUrl)
                }
            }
        }
    }

    //EditProfileActivity
    private fun uploadImageToStorage(uri: Uri, userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$userId.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val userRef = database.collection("user").document(userId)
                    userRef.update("profileImageUrl", downloadUri.toString())
                        .addOnSuccessListener {
                            Log.d("FirebaseDataManager", "Profile image updated")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FirebaseDataManager", "Error updating profile image", e)
                        }
                }
            }
    }

    //EditProfileActivity
    fun saveProfile(
        username: String,
        email: String,
        profileImageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = authManager.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = database.collection("user").document(userId)
            userRef.update(
                "name", username,
                "email", email
            )
                .addOnSuccessListener {
                    onSuccess()
                    profileImageUri?.let { uri ->
                        uploadImageToStorage(uri, userId)
                    }
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }
    }

    //fetch user data on userProfileActivity and UserFragment
    fun getUserData(
        userId: String,
        onDataLoaded: (username: String?, email: String?, profileImageUrl: String?) -> Unit
    ) {
        val userRef = database.collection("user").document(userId)

        // Use addSnapshotListener for real-time updates
        userRef.addSnapshotListener { document, e ->
            if (e != null) {
                Log.w("FirebaseDataManager", "Listen failed.", e)
                onDataLoaded(null, null, null)
                return@addSnapshotListener
            }

            if (document != null && document.exists()) {
                val username = document.getString("name")
                val email = document.getString("email")
                val profileImageUrl = document.getString("profileImageUrl")
                onDataLoaded(username, email, profileImageUrl)
            }
        }
    }




    //Follow User From UserProfile Fragment
    fun followUser(followedUserId: String) {
        val currentUser = authManager.getCurrentUser()
        currentUser?.let { user ->
            database.collection("user").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("name")
                    val image = document.getString("profileImageUrl")

                    val followedData = hashMapOf(
                        "userId" to user.uid,
                        "userName" to userName,
                        "image" to image
                    )
                    val followed =
                        database.collection("user").document(followedUserId).collection("followers")
                            .document(user.uid)
                    val following =
                        database.collection("user").document(user.uid).collection("following")
                            .document(followedUserId)

                    database.runTransaction { transaction ->
                        transaction.set(followed, followedData)
                        transaction.set(following, followedData)
                    }.addOnSuccessListener {
                        Toast.makeText(context, "Successfully followed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    //UnFollow user From UserProfileFragment
    fun unfollowUser(followedUserId: String) {
        val currentUser = authManager.getCurrentUser() ?: return
        val userId = currentUser.uid
        val followed =
            database.collection("user").document(followedUserId).collection("followers")
                .document(userId)
        val following = database.collection("user").document(userId).collection("following")
            .document(followedUserId)

        database.runTransaction { transaction ->
            transaction.delete(followed)
            transaction.delete(following)
        }.addOnSuccessListener {
            Toast.makeText(context, "Successfully Unfollowed!", Toast.LENGTH_SHORT).show()
        }
    }

    //Perform operation follow button
    fun isUserFollowed(followedUserId: String, callback: (Boolean) -> Unit) {
        val currentUser = authManager.getCurrentUser() ?: return
        val userId = currentUser.uid

        // Check if the current user is in the followers collection of the followed user
        database.collection("user")
            .document(followedUserId)
            .collection("followers")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                // If the document exists, the user is followed
                callback(document.exists())
            }

    }

    //fetch followers count on the basis of userId in UserProfileActivity
    fun getFollowersCount(userId: String, onCountFetched: (Int) -> Unit) {
        database.collection("user").document(userId).collection("followers")
            .addSnapshotListener { snapshot, error ->
                val count = snapshot?.documents?.size
                onCountFetched(count ?: 0)
            }
    }

    //fetch following count on the basis of userId in UserProfileActivity
    fun geFollwingCount(userId: String, onCountFetched: (Int) -> Unit) {

        database.collection("user").document(userId).collection("following").get()
            .addOnSuccessListener { document ->

                val count = document.size()
                onCountFetched(count)
            }
    }



    //fetch user data on userProfileActivity
    fun loadData(
        userId: String,
        onDataLoaded: (username: String?, email: String?, imageUrl: String?) -> Unit
    ) {
        val userRef = database.collection("user").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val username = document.getString("name")
                val email = document.getString("email")
                val imageUrl = document.getString("profileImageUrl")
                onDataLoaded(username, email, imageUrl)
            }
        }
    }

    //Group Fragment fetch group collection
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
