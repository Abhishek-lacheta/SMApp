package com.example.project01.adaptor


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project01.modal.HomeRecyclerModal
import com.example.project01.R
import java.text.SimpleDateFormat
import java.util.Locale

class HomeAdaptor(

    private val itemList: List<HomeRecyclerModal>,
    private val onFavClick: (HomeRecyclerModal) -> Unit
) : RecyclerView.Adapter<HomeAdaptor.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txttitel)
        val descTextView: TextView = itemView.findViewById(R.id.txtdes)
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val favorite: ImageView = itemView.findViewById(R.id.likeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_title_description, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTextView.text = item.title
        holder.descTextView.text = item.desc

        item.created_at?.let {
            val date = it.toDate()
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            holder.dateTextView.text = dateFormat.format(date)
        } ?: run {
            holder.dateTextView.text = "Date not available"
        }

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.imageView)
        holder.favorite.setImageResource(
            if (item.isFavorite) R.drawable.ic_fav else R.drawable.icon_favorite
        )
        holder.favorite.setOnClickListener {

            onFavClick(item)
        }
    }

    override fun getItemCount(): Int = itemList.size
}