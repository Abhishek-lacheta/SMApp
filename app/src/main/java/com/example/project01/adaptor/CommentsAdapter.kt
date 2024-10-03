package com.example.project01.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.R

class CommentsAdapter(
    private val comments: MutableList<String>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.Commentshow) // Change this ID based on your layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bottom_seat, parent, false) // Use your custom layout if necessary
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.commentText.text = comments[position]
    }

    override fun getItemCount(): Int = comments.size

    fun addComment(comment: String) {
        comments.add(comment)
        notifyItemInserted(comments.size - 1)
    }

    fun setComments(newComments: List<String>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }
}
