package com.example.project01.firebase

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseDatabaseManager {
    private val database= FirebaseFirestore.getInstance()

    //store data in user collection
    fun performSignUp2(uid: String?, name: String, email: String) {
        if (uid == null) return

        val userMap = hashMapOf(
            "name" to name,
            "email" to email
        )
        database.collection("user").document(uid).set(userMap)
    }
}