package com.example.project01.adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.project01.fragments.GroupsFragment
import com.example.project01.fragments.PostFragment
import com.example.project01.fragments.PostsFragment
import com.example.project01.fragments.UserFragment

class SearchPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments = listOf(
        PostsFragment(),
        GroupsFragment(),
    )

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Posts"
            1 -> "Groups"
            else -> null
        }
    }
}
