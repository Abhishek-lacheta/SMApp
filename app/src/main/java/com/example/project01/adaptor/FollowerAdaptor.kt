package com.example.project01.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.project01.R
import com.example.project01.modal.FollowerModal

class FollowerAdaptor(
    private val followers: MutableList<FollowerModal>
) : RecyclerView.Adapter<FollowerAdaptor.FollowerVierHolder>() {

    class FollowerVierHolder(item: View) : RecyclerView.ViewHolder(item) {

        val userName: TextView = item.findViewById(R.id.follwerName)
        val followerImage: ImageView = item.findViewById(R.id.FollowerProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerVierHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_follower, parent, false)
        return FollowerVierHolder(view)
    }

    override fun onBindViewHolder(holder: FollowerVierHolder, position: Int) {
        val follower = followers[position]
        holder.userName.text = follower.userName
        Glide.with(holder.itemView.context).load(follower.image).transform(CircleCrop())
            .into(holder.followerImage)
    }


    override fun getItemCount(): Int = followers.size

    fun setFollowers(newFollowers: List<FollowerModal>) {
        followers.clear()
        followers.addAll(newFollowers)
        notifyDataSetChanged()
    }
}