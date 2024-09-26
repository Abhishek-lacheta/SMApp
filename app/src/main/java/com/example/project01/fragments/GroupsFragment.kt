import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.project01.R
import com.example.project01.activity.AddPostGroupActivity
import com.example.project01.databinding.FragmentGroupsBinding
import com.example.project01.modal.GroupRecyclerModal
import com.google.firebase.firestore.FirebaseFirestore

class GroupsFragment : Fragment() {

    private lateinit var binding: FragmentGroupsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var groupRecyclerAdapter: GroupRecyclerAdapter
    private val itemList = mutableListOf<GroupRecyclerModal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root// Inflate the layout for this fragment
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity
        val toolbar: Toolbar = binding.toolbar
        activity.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Set up RecyclerView
        binding.groupRecyclerview.layoutManager =
            GridLayoutManager(context, 2)
        groupRecyclerAdapter = GroupRecyclerAdapter(itemList, ::onItemClick)
        binding.groupRecyclerview.adapter = groupRecyclerAdapter

        // Fetch data from Firestore
        fetchImages()


    }
    fun onItemClick(model: GroupRecyclerModal) {

        // Create a Bundle to pass the modal ID
        val bundle = Bundle().apply {
            putString("modalId", model.id)
            putString("name",model.name)
        }
        findNavController().navigate(R.id.addBlockFragment, bundle)
    }
    private fun fetchImages() {
        firestore.collection("group") // Adjust this path if needed
            .get()
            .addOnSuccessListener { result ->
                itemList.clear()
                for (document in result.documents) {
                    val item = document.toObject(GroupRecyclerModal::class.java)
                    item?.id = document.id
                    item?.let { itemList.add(it) }
                }
                groupRecyclerAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle possible errors.
                exception.printStackTrace()
            }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add1 -> {
                val intent = Intent(requireContext(), AddPostGroupActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}



