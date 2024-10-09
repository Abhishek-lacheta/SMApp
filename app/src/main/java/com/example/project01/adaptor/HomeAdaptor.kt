package com.example.project01.adaptor

import android.content.Intent
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
import com.example.project01.activity.UserProfileActivity
import java.text.SimpleDateFormat
import java.util.Locale

class HomeAdaptor(
    private val itemList: List<HomeModal>,
    private val onComment: (HomeModal) -> Unit,
    private val onShowPopupMenu: (View, HomeModal) -> Unit = { v, m -> },
    private val currentUserId: String?,
    private val onLikeClick: (HomeModal) -> Unit,
) : RecyclerView.Adapter<HomeAdaptor.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txttitel)
        val descTextView: TextView = itemView.findViewById(R.id.txtdes)
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val favorite: ImageButton = itemView.findViewById(R.id.likeButton)
        val likeCount: TextView = itemView.findViewById(R.id.likesCount)
        val showPopupMenu: ImageView = itemView.findViewById(R.id.showPopupMenu)
        val comment: ImageView = itemView.findViewById(R.id.Comment)
        val commentcount: TextView = itemView.findViewById(R.id.commentcount)
        val userprofile:ImageView=itemView.findViewById(R.id.userImageView)
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
        holder.commentcount.text = "${item.commentcount} comments" // Bind comment count

        item.created_at?.let {
            val date = it.toDate()
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            holder.dateTextView.text = dateFormat.format(date)
        } ?: run {
            holder.dateTextView.text = "Date not available"
        }

        holder.showPopupMenu.visibility = if (currentUserId == item.userId) View.VISIBLE else View.GONE

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.imageView)

        holder.favorite.setImageResource(
            if (item.isLikedByCurrentUser) R.drawable.ic_fav else R.drawable.icon_favorite
        )

        holder.favorite.setOnClickListener {
            onLikeClick(item)
        }

        holder.showPopupMenu.setOnClickListener {
            onShowPopupMenu(holder.showPopupMenu, item)
        }

        holder.comment.setOnClickListener {
            onComment(item)
        }
        holder.userprofile.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, UserProfileActivity::class.java).apply {
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = itemList.size
}




