package com.example.project01.viewmodal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project01.modal.HomeModal
import com.example.project01.repositoryfirebase.FirebaseRepositoryPost

class FavoriteViewModel : ViewModel() {

    private val repository = FirebaseRepositoryPost()
    private val _favoritePosts = MutableLiveData<List<HomeModal>>()
    val favoritePosts: LiveData<List<HomeModal>> = _favoritePosts

    fun getFavoritePosts() {
        repository.getFavoritePosts { fetchedList ->
            _favoritePosts.value = fetchedList
        }
    }
}
