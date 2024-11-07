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
import com.example.project01.modal.UserModal

class UserAdaptor(
    private val Users: MutableList<UserModal>,
    ) : RecyclerView.Adapter<UserAdaptor.UserVierHolder>() {

    class UserVierHolder(item: View) : RecyclerView.ViewHolder(item) {

        val userName: TextView = item.findViewById(R.id.follwerName)
        val followerImage: ImageView = item.findViewById(R.id.FollowerProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVierHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_follower, parent, false)
        return UserVierHolder(view)
    }

    override fun onBindViewHolder(holder: UserVierHolder, position: Int) {
        val User = Users[position]
        holder.userName.text = User.userName
        Glide.with(holder.itemView.context).load(User.image).transform(CircleCrop())
            .into(holder.followerImage)

    }

    override fun getItemCount(): Int = Users.size

    fun setUsers(newUsers: List<UserModal>) {
        Users.clear()
        Users.addAll(newUsers)
        notifyDataSetChanged()
    }
}