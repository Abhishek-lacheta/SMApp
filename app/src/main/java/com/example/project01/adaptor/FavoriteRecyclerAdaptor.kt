package com.example.project01.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project01.modal.TitelDescModal
import com.example.project01.R


class FavoriteRecyclerAdaptor( private val dataList: ArrayList<TitelDescModal> ) :

    RecyclerView.Adapter<FavoriteRecyclerAdaptor.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_title_description, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.txttitle.text = item.title
        holder.txtdesc.text = item.desc
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txttitle: TextView = itemView.findViewById(R.id.txttitel)
        val txtdesc: TextView = itemView.findViewById(R.id.txtdes)

    }
}
