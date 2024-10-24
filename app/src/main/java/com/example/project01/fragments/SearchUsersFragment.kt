package com.example.project01.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.project01.R
import com.example.project01.databinding.FragmentSearchUsersBinding


class SearchUsersFragment : Fragment() {
   private lateinit var binding: FragmentSearchUsersBinding
   

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_users, container, false)
    }


}