package com.example.project01.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database=FirebaseDatabaseManager()

    // SigUpActivity method Register a new user
    suspend fun registerUser(name: String,email: String, password: String): AuthResult {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        database.performSignUp2(task.result.user?.uid, name, email)
                        continuation.resume(task.result!!)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Registration failed")
                        )
                    }
                }
        }
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
    suspend fun forgotpassword(email: String):AuthResult{
        return suspendCoroutine { continuation ->

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener{task->

                    if (task.isSuccessful) {
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Sign in failed")
                        )
                    }

                }
        }
    }

    // Change user password
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
                                    continuation.resumeWithException(updateTask.exception ?: Exception("Password update failed"))
                                }
                            }
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Reauthentication failed"))
                    }
                }
        }
    }
}
