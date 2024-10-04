package com.example.project01.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project01.modal.HomeModal
import com.example.project01.R
import java.text.SimpleDateFormat
import java.util.Locale

class HomeAdaptor(
    private val itemList: List<HomeModal>,
    private val Oncomment: (HomeModal) -> Unit,
    private val onShowPopupMenu: (View, HomeModal) -> Unit = { v, m -> },
    private val currentUserId: String?,
    private val onLikeClick: (HomeModal) -> Unit // Added parameter for like click
) : RecyclerView.Adapter<HomeAdaptor.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txttitel)
        val descTextView: TextView = itemView.findViewById(R.id.txtdes)
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val favorite: ImageButton = itemView.findViewById(R.id.likeButton)
        val likeCount: TextView = itemView.findViewById(R.id.likesCount)
        val showPopupMenu: ImageView = itemView.findViewById(R.id.showPopupMenu)
        val comment:ImageView=itemView.findViewById(R.id.Comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comman, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        holder.titleTextView.text = item.title
        holder.descTextView.text = item.desc
        holder.likeCount.text = "${item.likeCount} likes" // Set like count text

        item.created_at?.let {
            val date = it.toDate()
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            holder.dateTextView.text = dateFormat.format(date)
        } ?: run {
            holder.dateTextView.text = "Date not available"
        }

        if (currentUserId == item.userId) {
            holder.showPopupMenu.visibility = View.VISIBLE
        } else {
            holder.showPopupMenu.visibility = View.GONE
        }
        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.imageView)
        // Update like button based on whether the user liked the post
        holder.favorite.setImageResource(
            if (item.isLikedByCurrentUser) R.drawable.ic_fav else R.drawable.icon_favorite
        )
        // Handle like button click
        holder.favorite.setOnClickListener {
            onLikeClick(item)
        }
        // Show popup menu on click
        holder.showPopupMenu.setOnClickListener {
            onShowPopupMenu(holder.showPopupMenu, item)
        }

        holder.comment.setOnClickListener {
            Oncomment(item)
        }

    }
    override fun getItemCount(): Int = itemList.size
}




