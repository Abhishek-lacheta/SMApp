package com.example.project01.viewmodal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project01.modal.HomeModal
import com.example.project01.repositoryfirebase.FirebaseRepositoryPost

class PostViewModel : ViewModel() {
    private val repositoryPost = FirebaseRepositoryPost(

    )
    private val _posts = MutableLiveData<ArrayList<HomeModal>>()
    val posts: LiveData<ArrayList<HomeModal>> = _posts

    private val _isPostDeleted = MutableLiveData<Boolean>()
    val isPostDeleted: LiveData<Boolean> get() = _isPostDeleted

    fun getPost(userId: String, modalId: String) {
        repositoryPost.getPostByGroup(userId,modalId) { fetchList ->
            _posts.value = fetchList
        }
    }
    fun deletePost(documentId: String) {
        repositoryPost.deletePost(documentId) { success ->
            _isPostDeleted.value = success  // Update LiveData with success or failure
        }
    }
    // Remove post from list
    fun removeFromList(item: HomeModal, posts: ArrayList<HomeModal>) {
        posts.remove(item)
    }
}