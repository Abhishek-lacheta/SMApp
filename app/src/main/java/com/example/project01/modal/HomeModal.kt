package com.example.project01.modal

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Comment


data class HomeModal(
    val title: String? = null,
    val desc: String? = null,
    val imageUrl: String? = null,
    val created_at: Timestamp? = null,
    var id: String? = null,
    var likeCount: Int = 0,
    var userId: String? = null,
    var groupId: String?=null,
    var likedBy: List<String> = listOf()
) {
    val isLikedByCurrentUser: Boolean
        get() = likedBy.contains(FirebaseAuth.getInstance().currentUser?.uid)
}


