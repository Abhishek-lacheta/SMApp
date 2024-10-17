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
import com.example.project01.modal.FollowingModal

 class FollowingAdaptor (
    private val followings: MutableList<FollowingModal>
) : RecyclerView.Adapter<FollowingAdaptor.FollowingVierHolder>() {


    class FollowingVierHolder(item: View) : RecyclerView.ViewHolder(item) {

        val userName: TextView = item.findViewById(R.id.FollowingName)
        val followingImage: ImageView = item.findViewById(R.id.FollowingProfile)
    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingVierHolder {
         val view = LayoutInflater.from(parent.context)
             .inflate(R.layout.item_following, parent, false)
         return FollowingVierHolder(view)
     }

     override fun onBindViewHolder(holder: FollowingVierHolder, position: Int) {
         val following = followings[position]
         holder.userName.text = following.userName
         Glide.with(holder.itemView.context).load(following.image).transform(CircleCrop())
             .into(holder.followingImage)
     }
     override fun getItemCount(): Int = followings.size

     fun setFollowings(newFollowing: MutableList<FollowingModal>) {
         followings.clear()
         followings.addAll(newFollowing)
         notifyDataSetChanged()
     }
}