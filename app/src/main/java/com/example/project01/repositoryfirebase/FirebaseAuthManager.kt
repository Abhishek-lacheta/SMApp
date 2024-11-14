package com.example.project01.repositoryfirebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = FirebaseFirestore.getInstance()


    // SigUpActivity method Register a new user
    suspend fun registerUser(name: String, email: String, password: String): AuthResult {

        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        performSignUp2(task.result.user?.uid, name, email)
                        continuation.resume(task.result!!)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Registration failed")
                        )
                    }
                }
        }
    }


    fun performSignUp2(uid: String?, name: String, email: String) {
        if (uid == null) return
        val name_Lc = name.lowercase()
        val userMap = hashMapOf(
            "name" to name,
            "name_Lc" to name_Lc,
            "email" to email
        )
        database.collection("user").document(uid).set(userMap)
    }

    // Login Activity mrthod existing user
    suspend fun signInUser(email: String, password: String): AuthResult {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(task.result!!)

                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Sign in failed")
                        )
                    }
                }
        }
    }

    //ForgotPassword Activity
    suspend fun forgotpassword(email: String): AuthResult {
        return suspendCoroutine { continuation ->

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Sign in failed")
                        )
                    }

                }
        }
    }

    // ChangePasswordActivity Change user password
    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        val user = auth.currentUser ?: throw Exception("No user is currently signed in.")
        return suspendCoroutine { continuation ->
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    continuation.resume(true)
                                } else {
                                    continuation.resumeWithException(
                                        updateTask.exception ?: Exception("Password update failed")
                                    )
                                }
                            }
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Reauthentication failed")
                        )
                    }
                }
        }
    }

    //CurrentUser get krne ka liye
    fun getCurrentUser() = auth.currentUser

    // logout ke liye
    fun signout(onSignout: () -> Unit) {
        auth.signOut()
        onSignout()

    }
}
