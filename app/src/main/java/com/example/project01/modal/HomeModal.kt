package com.example.project01.modal

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.parcelize.Parcelize
import org.w3c.dom.Comment


@Parcelize
 class HomeModal (
    val title: String? = null,
    val desc: String? = null,
    val imageUrl: String? = null,
    val created_at: Timestamp? = null,
    var id: String? = null,
    var likeCount: Int = 0,
    var commentcount:Int=0,
    var userId: String? = null,
    var groupId: String?=null,
    var likedBy: List<String> = listOf()
):Parcelable {
    val isLikedByCurrentUser: Boolean
        get() = likedBy.contains(FirebaseAuth.getInstance().currentUser?.uid)
}


