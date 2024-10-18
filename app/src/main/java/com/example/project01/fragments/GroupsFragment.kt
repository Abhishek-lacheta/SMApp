import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.project01.R
import com.example.project01.activity.AddPostGroupActivity
import com.example.project01.databinding.FragmentGroupsBinding
import com.example.project01.firebase.FirebaseAuthManager
import com.example.project01.firebase.FirebaseDatabaseManager
import com.example.project01.modal.GroupModal

class GroupsFragment : Fragment() {

    private lateinit var binding: FragmentGroupsBinding
    private lateinit var groupRecyclerAdapteradaptor: GroupRecyclerAdapter
    private val itemList = mutableListOf<GroupModal>()
    private lateinit var databaseManager: FirebaseDatabaseManager
    private var authManager = FirebaseAuthManager()
    private lateinit var noDataLayout: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root // Inflate the layout for this fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity
        val toolbar: Toolbar = binding.toolbar
        activity.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        // Initialize FirebaseDatabaseManager
        databaseManager = FirebaseDatabaseManager(requireContext())

        // Set up RecyclerView
        binding.groupRecyclerview.layoutManager = GridLayoutManager(context, 2)
        noDataLayout = binding.noDataLayout
        // Fetch data from Firestore
        fetchGroupData()
        setupToolbar()
    }

    //backicon ke liye
    private fun setupToolbar() {

        val openFromUserfragment = arguments?.getBoolean("openFromUserfragment") ?: false
        if (openFromUserfragment) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_arrovback)
            binding.toolbar.setNavigationOnClickListener {

                findNavController().popBackStack()
            }
        } else {
            binding.toolbar.navigationIcon = null
        }
    }

    //SetUp RecyclerView
    private fun setupRecyclerView() {
        groupRecyclerAdapteradaptor = GroupRecyclerAdapter(
            itemList = itemList,
            onGroupPopupMenu = { view, item -> showPopupMenu(view, item) },
            onItemClick = { itemList -> onItemClick(itemList) },
            isPopupMenuVisible = true
        )
        binding.groupRecyclerview.adapter = groupRecyclerAdapteradaptor
    }

    //delete data
    private fun deleteItem(item: GroupModal): Boolean {
        item.id?.let { documentId ->
            databaseManager.deleteGroupData(documentId) { success ->
                if (success) {
                    itemList.remove(item)
                    groupRecyclerAdapteradaptor.notifyDataSetChanged()
                    Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(context, "Item ID is null", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    //showPopumMenu
    private fun showPopupMenu(view: View, item: GroupModal) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.confirm_delete -> deleteItem(item)
                R.id.update -> {
                    val intent = Intent(requireContext(), AddPostGroupActivity::class.java).apply {
                        putExtra("group", item)
                    }
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    //Navigate to AddBloackFragment
    fun onItemClick(model: GroupModal) {
        val bundle = Bundle().apply {
            putString("modalId", model.id)
            putString("name", model.name)
            putString("userId", authManager.getCurrentUser()?.uid)
            Log.d(
                "groupFragment",
                "Item clicked: ${model.id}, ${model.name},${authManager.getCurrentUser()?.uid}"
            )
        }
        val navOptions =
            NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build()

        findNavController().navigate(R.id.addBlockFragment, bundle, navOptions)
    }

    //Fetch GroupData
    private fun fetchGroupData() {
        databaseManager.fetchDataGroupFromeFireStore { fetchedList ->
            if (fetchedList.isEmpty()) {
                noDataLayout.visibility = View.VISIBLE
                binding.groupRecyclerview.visibility = View.GONE
            } else {
                noDataLayout.visibility = View.GONE
                binding.groupRecyclerview.visibility = View.VISIBLE
                itemList.clear()
                itemList.addAll(fetchedList)
                setupRecyclerView()
            }

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



