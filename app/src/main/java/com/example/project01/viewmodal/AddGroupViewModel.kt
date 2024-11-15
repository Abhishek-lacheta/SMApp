package com.example.project01.viewmodal

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project01.repositoryfirebase.FirebaseRepositoryGroup

class AddGroupViewModel : ViewModel() {
    private val grouprepository = FirebaseRepositoryGroup()
    private val _isGroupSaved = MutableLiveData<Boolean>()
    val isGroupSaved: LiveData<Boolean> = _isGroupSaved

    fun saveGroup(imageUri: Uri, name: String) {
        grouprepository.saveGroup(imageUri, name) { saveGroup ->
            _isGroupSaved.value = saveGroup
        }
    }
    fun updateGroup(groupId: String, name: String, imageUri: Uri) {

        grouprepository.updateData(groupId, name, imageUri) { updateGroup ->
            _isGroupSaved.value = updateGroup
        }
    }

}