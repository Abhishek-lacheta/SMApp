package com.example.project01.adaptor

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project01.fragments.SearchPostFragment

import com.example.project01.fragments.SearchUsersFragment
import com.example.project01.fragments.SearchgroupsFragment

class SearchPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentList = listOf(
        SearchPostFragment(),
        SearchgroupsFragment(),
        SearchUsersFragment()
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun getCurrentFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}