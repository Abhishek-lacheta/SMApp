package com.example.project01.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.project01.modal.GroupModal
import com.example.project01.modal.HomeModal
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class FirebaseDatabaseManager(private val context: Context) {
    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var authManager = FirebaseAuthManager()

    fun searchPosts(searchQuery: String, callback: (List<HomeModal>) -> Unit) {
        // Normalize searchQuery to lowercase for case-insensitive search
        val lowerCaseQuery = searchQuery.lowercase()

        // Debugging logs
        Log.d("FirestoreQuery", "Searching for: $lowerCaseQuery")

        val query = database.collection("home")
            .whereGreaterThanOrEqualTo("title", lowerCaseQuery)
            .whereLessThanOrEqualTo("title", lowerCaseQuery + "\uf8ff")
        query.get()
            .addOnSuccessListener { result ->
                Log.d("FirestoreQuery", "Query successful, found ${result.size()} results.")
                val dataList = result.documents.mapNotNull { document ->
                    document.toObject(HomeModal::class.java)
                }
                callback(dataList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting documents: ", exception)
                callback(emptyList())
            }
    }


    fun searchGroups(searchQuery: String, callback: (List<GroupModal>) -> Unit) {
        // Normalize searchQuery to lowercase for case-insensitive search
        val lowerCaseQuery = searchQuery.lowercase()

        // Debugging logs
        Log.d("FirestoreQuery", "Searching for: $lowerCaseQuery")

        val query = database.collection("group")
            .whereGreaterThanOrEqualTo("name", lowerCaseQuery)
            .whereLessThanOrEqualTo("name", lowerCaseQuery + "\uf8ff")
        query.get()
            .addOnSuccessListener { result ->
                Log.d("FirestoreQuery", "Query successful, found ${result.size()} results.")
                val dataList = result.documents.mapNotNull { document ->
                    document.toObject(GroupModal::class.java)
                }
                callback(dataList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting documents: ", exception)
                callback(emptyList())
            }
    }



    //Home fragment fetch home collection
    fun fetchDataHomeFromFireStore(callback: (List<HomeModal>) -> Unit) {
        database.collection("home").get()
            .addOnSuccessListener { result ->
                val dataList = ArrayList<HomeModal>()

                for (document in result.documents) {
                    val item = document.toObject(HomeModal::class.java)
                    item?.let {
                        it.id = document.id // Set the document ID
                        dataList.add(it)
                    }
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

    // favrate Fragment fetch home data
    fun fetchFavoriteItemsFromFirebase(callback: (List<HomeModal>) -> Unit) {
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


    //MultipalGroupFragment fetch data groupById
    fun fetchDataByGroupId(userId: String, groupId: String, callback: (List<HomeModal>) -> Unit) {
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
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error fetching data: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                callback(emptyList())
            }
    }

    // MultipalGroupFragment Delete a document
    fun deleteData(documentId: String, callback: (Boolean) -> Unit) {
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

    //GropFragment delele dacument
    fun deleteGroupData(documentId: String, callback: (Boolean) -> Unit) {
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


    // AddPostGroupActivity image upload and save data
    fun uploadImageAndSaveData(imageUri: Uri, name: String, callback: (Boolean) -> Unit) {
        val homeMap = hashMapOf("name" to name, "userId" to authManager.getCurrentUser()?.uid)
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
    private fun saveGroupData(homeMap: HashMap<String, String?>, callback: (Boolean) -> Unit) {
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

    // AddPostHomeActivity Upload image and save home data
    fun uploadImageAndSaveData(
        imageUri: Uri,
        title: String,
        desc: String,
        selectedGroupId: String?,
        isFavorite: Boolean,
        callback: (Boolean) -> Unit
    ) {
        val currentUser = authManager.getCurrentUser()
        currentUser?.let { user ->
            database.collection("user").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("name") ?: "Unknown User"
                    val image = document.getString("profileImageUrl")
                    val homeMap = hashMapOf(
                        "title" to title,
                        "desc" to desc,
                        "created_at" to FieldValue.serverTimestamp(),
                        "groupId" to (selectedGroupId ?: ""),
                        "isFavorite" to isFavorite,
                        "userId" to user.uid,
                        "userName" to userName,
                        "image" to image
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
                }

        }
    }

    //AddPostHomeActivity save data
    private fun saveHomeData(homeMap: HashMap<String, Any?>, callback: (Boolean) -> Unit) {
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

    // Update group collection from AddpostGroupActivity
    fun updateGroupData(groupId: String, name: String, imageUri: Uri, callback: (Boolean) -> Unit) {
        val groupUpdate = hashMapOf<String, Any>(
            "name" to name,
        )

        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    groupUpdate["imageUrl"] = downloadUri.toString()
                    // Pass groupId along with groupUpdate
                    saveData(groupId, groupUpdate, callback)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                callback(false) // Notify failure
            }
    }

    // sava update groupData
    private fun saveData(
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

    //save updated Home Data
    private fun saveUpdateData(
        postId: String,
        postUpdates: HashMap<String, Any>,
        callback: (Boolean) -> Unit
    ) {
        database.collection("home").document(postId)
            .update(postUpdates)
            .addOnSuccessListener {

                callback(true)
            }
            .addOnFailureListener { e ->
                Log.w("FirebaseDatabaseManager", "Error updating document", e)
                callback(false)
            }
    }

    // Update Data in Home collection  From AddGroupActivity
    fun updateData(
        postId: String,
        imageUri: Uri,
        title: String,
        description: String,

        callback: (Boolean) -> Unit
    ) {
        val postUpdates = hashMapOf<String, Any>(
            "title" to title,
            "desc" to description
        )
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    postUpdates["imageUrl"] = downloadUri.toString()
                    // Pass groupId along with groupUpdate
                    saveUpdateData(postId, postUpdates, callback)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                callback(false) // Notify failure
            }
    }

    //AddPostHomeActivity Fetch group data
    fun fetchGroupData(callback: (List<Pair<String, String>>) -> Unit) {
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

            userRef.update("name", username, "email", email)
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
    fun getUserData(userId: String, onDataLoaded: (username: String?, email: String?, profileImageUrl: String?) -> Unit) {
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


    // fetch group data on the basis of userId in UserProfileActivity
    fun fetchDataGroupFromeFireStore1(userId: String, callback: (List<GroupModal>) -> Unit) {
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

    // fetch group count on the basis of userId in UserProfileActivity
    fun fetchGroupCountFromFirestore(userId: String, callback: (Int) -> Unit) {
        database.collection("group").whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val groupCount = result.size()
                callback(groupCount)
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
