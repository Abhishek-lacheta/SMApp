import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project01.R
import com.example.project01.modal.GroupModal

class GroupRecyclerAdapter(
    private val itemList: List<GroupModal>,
    private val onItemClick: (GroupModal) -> Unit
) :
    RecyclerView.Adapter<GroupRecyclerAdapter.GroupViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val item = itemList[position]
        holder.textView.text = item.name
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val textView: TextView = itemView.findViewById(R.id.text_view)

    }

    override fun getItemCount(): Int = itemList.size
}
