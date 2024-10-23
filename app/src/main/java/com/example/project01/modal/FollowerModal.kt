package com.example.project01.modal

data class FollowerModal(
    val userName: String,
    val image: String?,
) {
    var isFollowed: Boolean = false // Follow status
}