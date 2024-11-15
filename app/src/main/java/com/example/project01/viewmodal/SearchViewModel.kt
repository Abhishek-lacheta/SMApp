package com.example.project01.viewmodal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project01.modal.HomeModal
import com.example.project01.repositoryfirebase.FirebaseRepositorySearch

class SearchViewModel : ViewModel() {

    private val searchrepositery = FirebaseRepositorySearch()

    private val _searchPost = MutableLiveData<List<HomeModal>>()
    val searchPost: LiveData<List<HomeModal>> = _searchPost

    fun searchPosts(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            searchrepositery.searchPosts(searchQuery) { results ->
                _searchPost.postValue(results)
            }
        } else {
            _searchPost.postValue(emptyList())
        }
    }
}