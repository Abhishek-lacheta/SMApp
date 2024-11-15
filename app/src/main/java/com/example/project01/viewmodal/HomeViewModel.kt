package com.example.project01.viewmodal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project01.modal.HomeModal
import com.example.project01.repositoryfirebase.FirebaseRepositoryPost

class HomeViewModel : ViewModel() {
    private val repository = FirebaseRepositoryPost(

    )
    private val _Posts = MutableLiveData<List<HomeModal>>()
    val Posts: LiveData<List<HomeModal>> = _Posts

    fun getPosts() {
        repository.getPosts { fetchList ->
            _Posts.value = fetchList
        }
    }
}