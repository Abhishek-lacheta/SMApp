package com.example.project01.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.R
import com.example.project01.modal.Comment

class CommentsAdapter(
    private val comments: MutableList<Comment>
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.Commentshow)
        val userNameText: TextView = itemView.findViewById(R.id.username)
        val commentDateText: TextView = itemView.findViewById(R.id.commentDate) // Add date TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bottom_seat, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.commentText.text = comment.text
        holder.userNameText.text = comment.userName
        holder.commentDateText.text = getTimeAgo(comment.timestamp) // Display relative time
    }

    override fun getItemCount(): Int = comments.size

    fun addComment(comment: Comment) {
        comments.add(comment)
        notifyItemInserted(comments.size - 1)
    }

    fun setComments(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    // Function to get relative time
    private fun getTimeAgo(time: Long): String {
        val now = System.currentTimeMillis()
        val seconds = (now - time) / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            seconds < 60 -> "$seconds s ago"
            minutes < 60 -> "$minutes m ago"
            hours < 24 -> "$hours h ago"
            days < 7 -> "$days d ago"
            weeks < 5 -> "$weeks w ago"
            months < 12 -> "$months mo ago"
            else -> "$years y ago"
        }
    }
}



