package com.example.project01.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.R
import com.example.project01.adaptor.CommentsAdapter
import com.example.project01.databinding.FragmentCommentsBinding
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.modal.Comment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

open class CommentsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCommentsBinding
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var postId: String
    private var authManager = FirebaseAuthManager()
    private val commentsAdapter = CommentsAdapter(mutableListOf())
    private val db: FirebaseFirestore = Firebase.firestore
    private var lastVisible: DocumentSnapshot? = null
    private val PAGE_SIZE = 20  // Number of comments per page

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get postId from Home Fragment
        arguments?.let {
            postId = it.getString("postId", "")
        }

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView)
        commentInput = view.findViewById(R.id.commentInput)
        sendButton = view.findViewById(R.id.sendButton)

        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = commentsAdapter

        sendButton.setOnClickListener {
            val commentText = commentInput.text.toString()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
                commentInput.text.clear()
                commentsRecyclerView.scrollToPosition(commentsAdapter.itemCount - 1)
            }
        }

        loadComments()
    }

    private fun addComment(comment: String) {
        val currentUser = authManager.getCurrentUser()
        currentUser?.let { user ->
            db.collection("user").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("name") ?: "Unknown User"
                    val image = document.getString("profileImageUrl")
                    val currentTimeMillis =
                        System.currentTimeMillis() // Get current time in milliseconds

                    val commentData = hashMapOf(
                        "text" to comment,
                        "userId" to user.uid,
                        "userName" to userName,
                        "timestamp" to currentTimeMillis,
                        "image" to image
                    )
                    db.collection("home").document(postId).collection("comments")
                        .add(commentData)
                        .addOnSuccessListener {
                            // After successfully adding the comment, fetch the new count
                            db.collection("home").document(postId).collection("comments")
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val newCount = querySnapshot.size()
                                    // Update comment count in the parent fragment
                                    (parentFragment as? HomeFragment)?.getCommentCount(
                                        postId,
                                        newCount
                                    )
                                }

                            // Update commentsAdapter with the new comment
                            commentsAdapter.addComment(
                                Comment(
                                    text = comment,
                                    userName = userName,
                                    timestamp = currentTimeMillis // Pass timestamp

                                )
                            )
                        }
                }
        }
    }

    private fun loadComments(isNextPage: Boolean = false) {
        var query = db.collection("home").document(postId).collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(20)

        // If loading the next page, use startAfter to get documents after the last one
        if (isNextPage && lastVisible != null) {
            query = query.startAfter(lastVisible!!)
        }
        query.get()
            .addOnSuccessListener { documents ->
                val comments = mutableListOf<Comment>()
                // Loop through the documents and add them to the comments list
                for (document in documents) {
                    val text = document.getString("text") ?: ""
                    val userName = document.getString("userName") ?: "Unknown User"
                    val profileImageUrl = document.getString("image") // Retrieve the image URL
                    val timestamp = document.getLong("timestamp") ?: 0L

                    // Add the profileImageUrl to the Comment instance
                    comments.add(Comment(text, userName, timestamp, profileImageUrl))
                }

                // Update the 'lastVisible' variable for the next page
                lastVisible = documents.documents[documents.size() - 1]

                // Set the comments in the adapter (append new comments to the existing list)
                commentsAdapter.setComments(comments)

            }
    }
}

