package com.example.project01.adaptor

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project01.fragments.SearchgroupsFragment
import com.example.project01.fragments.SearchPostFragment
import com.example.project01.fragments.SearchUsersFragment

class SearchPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        SearchPostFragment(),
        SearchgroupsFragment(),
        SearchUsersFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getFragment(position: Int): Fragment {
        return fragments[position]
    }
}
