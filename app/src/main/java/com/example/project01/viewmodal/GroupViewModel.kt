package com.example.project01.viewmodal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project01.modal.GroupModal
import com.example.project01.repositoryfirebase.FirebaseRepositoryGroup

class GroupViewModel : ViewModel() {
    private val grouprepository = FirebaseRepositoryGroup()

    private val _groupList = MutableLiveData<List<Pair<String, String>>>()
    val groupList: LiveData<List<Pair<String, String>>> = _groupList


    private val _grops = MutableLiveData<ArrayList<GroupModal>>()
    val group: LiveData<ArrayList<GroupModal>> = _grops

    private val _isGroupDeleted = MutableLiveData<Boolean>()
    val isGroupDeleted: LiveData<Boolean> get() = _isGroupDeleted

    fun fetchGroups(userId: String) {
        grouprepository.getGroups(userId) { fetchgroupList ->
            _grops.value = fetchgroupList

        }
    }

    // group ke name ko get kiya hai by group Id AddPostActivity pr
    fun getGroups() {
        grouprepository.getGroup { fetchgroups ->
            _groupList.value = fetchgroups

        }
    }

    fun deleteGroup(documentId: String) {
        grouprepository.deleteGroup(documentId) { deleteGroup ->
            _isGroupDeleted.value = deleteGroup
        }
    }

    // Remove post from list
    fun removeFromList(item: GroupModal, groups: ArrayList<GroupModal>) {
        groups.remove(item)
    }

}