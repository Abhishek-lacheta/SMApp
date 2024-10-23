import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.project01.R
import com.example.project01.adaptor.SearchPagerAdapter
import com.example.project01.fragments.GroupsFragment
import com.example.project01.fragments.PostsFragment

class SearchFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: SearchPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        viewPager = view.findViewById(R.id.view_pager)
        pagerAdapter = SearchPagerAdapter(childFragmentManager)
        viewPager.adapter = pagerAdapter

        val searchView: SearchView = view.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    (pagerAdapter.getItem(0) as PostsFragment).filter(it)
                    (pagerAdapter.getItem(1) as GroupsFragment).filter(it)
                }
                return false
            }
        })

        return view
    }
}
