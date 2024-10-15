package com.example.project01.modal

data class Comment(
    val text: String,
    val userName: String,
    val timestamp: Long,
    val profileImageUrl: String?=null
)