package com.example.project01.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.R
import com.example.project01.adaptor.CommentsAdapter
import com.example.project01.databinding.BottomSeatDialogBinding
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.modal.Comment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

open class BottomSeatFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSeatDialogBinding
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var postId: String
    private var authManager = FirebaseAuthManager()
    private val commentsAdapter = CommentsAdapter(mutableListOf())
    private val db: FirebaseFirestore = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSeatDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    val userName = document.getString("name") ?: "Unknown User" // Default name
                    val currentTimeMillis = System.currentTimeMillis() // Get current time in milliseconds

                    val commentData = hashMapOf(
                        "text" to comment,
                        "userId" to user.uid,
                        "userName" to userName,
                        "timestamp" to currentTimeMillis // Store timestamp
                    )
                    db.collection("home").document(postId).collection("comments")
                        .add(commentData)
                        .addOnSuccessListener { documentReference ->
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

    private fun loadComments() {
        db.collection("home").document(postId).collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val comments = mutableListOf<Comment>()
                for (document in documents) {
                    val text = document.getString("text") ?: ""
                    val userName = document.getString("userName") ?: "Unknown User"
                    val timestamp = document.getLong("timestamp") ?: 0L

                    comments.add(Comment(text, userName, timestamp))
                }
                commentsAdapter.setComments(comments)
            }
    }
}

