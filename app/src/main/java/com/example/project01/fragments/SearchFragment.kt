package com.example.project01.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import com.example.project01.adaptor.SearchPagerAdapter
import com.example.project01.databinding.FragmentSearchBinding
import com.google.android.material.tabs.TabLayoutMediator

class SearchFragment : Fragment() {

    private lateinit var pagerAdapter: SearchPagerAdapter
    private lateinit var binding: FragmentSearchBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
       binding=FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.visibility = View.GONE
        binding.tabLayout.visibility = View.GONE
        binding.noDataLayout.visibility = View.VISIBLE

        // Set up the pager adapter for ViewPager2
        pagerAdapter = SearchPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Set up TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Posts"
                1 -> tab.text = "Groups"
                2 -> tab.text = "Users"
            }
        }.attach()

        // Set query listener on the SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // Trigger search in the currently visible fragment
                    val currentFragment = pagerAdapter.getCurrentFragment(binding.viewPager.currentItem)
                    if (currentFragment is Searchable) {
                        currentFragment.search(it)
                    }
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText.isNullOrEmpty()) {
                    binding.viewPager.visibility = View.GONE
                    binding.tabLayout.visibility = View.GONE
                    binding.noDataLayout.visibility = View.VISIBLE
                } else {

                    binding.viewPager.visibility = View.VISIBLE
                    binding.tabLayout.visibility = View.VISIBLE
                    binding.noDataLayout.visibility = View.GONE
                }
                return true
            }
        })
    }
}


