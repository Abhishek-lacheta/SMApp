import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.project01.R
import com.example.project01.adaptor.SearchPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SearchFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: SearchPagerAdapter
    private lateinit var tabLayout: TabLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.searchtoolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Initialize ViewPager and TabLayout
        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)

        pagerAdapter = SearchPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Posts"
                1 -> tab.text = "Groups"
                2->tab.text="Users"
            }
        }.attach()

        val searchView: SearchView = view.findViewById(R.id.search_view)


        searchView.setOnClickListener {
            searchView.isIconified = false
        }

        return view
    }
}

