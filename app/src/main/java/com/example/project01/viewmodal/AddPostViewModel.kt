package com.example.project01.viewmodal

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project01.modal.GroupModal
import com.example.project01.repositoryfirebase.FirebaseRepositoryGroup
import com.example.project01.repositoryfirebase.FirebaseRepositoryPost
import kotlinx.coroutines.launch

class AddPostViewModel:ViewModel() {
    private val repository = FirebaseRepositoryPost()
    private val grouprepository=FirebaseRepositoryGroup()
    private val _isPostSaved = MutableLiveData<Boolean>()
    val isPostSaved: LiveData<Boolean> = _isPostSaved
    private val _groupList = MutableLiveData<List<Pair<String, String>>>()
    val groupList: LiveData<List<Pair<String, String>>> = _groupList

    fun getGroups(){
       grouprepository.getGroup { fetchgroups->
           _groupList.value=fetchgroups

       }
   }

    // Function to save data
    fun saveData(
        imageUri: Uri,
        title: String,
        desc: String,
        selectedGroupId: String?,
        isFavorite: Boolean,
        linkAddress: String
    ) {
        viewModelScope.launch {
            repository.savePost(imageUri, title, desc, selectedGroupId, isFavorite, linkAddress) { success ->
                _isPostSaved.postValue(success)
            }
        }
    }

    // Function to update existing post data
    fun updateData(
        postId: String,
        imageUri: Uri,
        title: String,
        description: String
    ) {
        viewModelScope.launch {
            repository.updateData(postId, imageUri, title, description) { success ->
                _isPostSaved.postValue(success)  // Update LiveData with the success/failure status
            }
        }
    }
}