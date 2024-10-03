package com.example.project01.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.R
import com.example.project01.adaptor.CommentsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

open class BottomSeatFragment : BottomSheetDialogFragment() {

    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var postId: String

    private val commentsAdapter = CommentsAdapter(mutableListOf())
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments.let {
            if (it != null) {
                postId = it.getString("postId", "")
            }
        }
        val view = inflater.inflate(R.layout.bottom_seat_dialog, container, false)

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

        return view
    }

    private fun addComment(comment: String) {
        val commentData = hashMapOf("text" to comment)
        db.collection("home").document(postId).collection("comments")
            .add(commentData)
            .addOnSuccessListener {

                commentsAdapter.addComment(comment)
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }

    private fun loadComments() {
        db.collection("home").document(postId).collection("comments")
            .get()
            .addOnSuccessListener { result ->
                val comments = mutableListOf<String>()
                for (document in result) {
                    val commentText1 = document.getString("text")
                    if (commentText1 != null) {
                        comments.add(commentText1)
                    }
                }
                commentsAdapter.setComments(comments)
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }
}