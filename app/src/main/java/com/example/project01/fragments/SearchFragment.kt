package com.example.project01.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.widget.ViewPager2
import com.example.project01.R
import com.example.project01.adaptor.SearchPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SearchFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: SearchPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var searchView: SearchView
    private lateinit var noDataLayout: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize views
        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)
        searchView = view.findViewById(R.id.search_view)
        noDataLayout = view.findViewById(R.id.noDataLayout)

        // Initially hide ViewPager, TabLayout, and NoDataLayout
        viewPager.visibility = View.GONE
        tabLayout.visibility = View.GONE
        noDataLayout.visibility = View.VISIBLE

        // Set up the pager adapter for ViewPager2
        pagerAdapter = SearchPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Set up TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Posts"
                1 -> tab.text = "Groups"
                2 -> tab.text = "Users"
            }
        }.attach()

        // Set query listener on the SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // Trigger search in the currently visible fragment
                    val currentFragment = pagerAdapter.getCurrentFragment(viewPager.currentItem)
                    if (currentFragment is Searchable) {
                        currentFragment.search(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText.isNullOrEmpty()) {
                    viewPager.visibility = View.GONE
                    tabLayout.visibility = View.GONE
                    noDataLayout.visibility = View.VISIBLE
                } else {

                    viewPager.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                    noDataLayout.visibility = View.GONE
                }
                return true
            }
        })

        return view
    }

}


