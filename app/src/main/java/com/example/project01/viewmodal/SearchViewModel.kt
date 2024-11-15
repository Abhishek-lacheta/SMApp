package com.example.project01.viewmodal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project01.modal.GroupModal
import com.example.project01.modal.HomeModal
import com.example.project01.modal.UserModal
import com.example.project01.repositoryfirebase.FirebaseRepositorySearch

class SearchViewModel : ViewModel() {

    private val searchrepositery = FirebaseRepositorySearch()

    private val _searchPost = MutableLiveData<List<HomeModal>>()
    val searchPost: LiveData<List<HomeModal>> = _searchPost

    private val _searchGroup=MutableLiveData<List<GroupModal>>()
    val searchGroup:LiveData<List<GroupModal>> = _searchGroup

    private val _searchUser=MutableLiveData<List<UserModal>>()
    val searchUser:LiveData<List<UserModal>> = _searchUser

    fun searchPosts(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            searchrepositery.searchPosts(searchQuery) { results ->
                _searchPost.postValue(results)
            }
        } else {
            _searchPost.postValue(emptyList())
        }
    }

    fun searchGroup(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            searchrepositery.searchGroups(searchQuery) { results ->
                _searchGroup.value=results
            }
        } else {
            _searchGroup.postValue(emptyList())
        }
    }
    fun searcUser(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            searchrepositery.searchUsers(searchQuery) { results ->
                _searchUser.value=results
            }
        } else {
            _searchUser.postValue(emptyList())
        }
    }
}


