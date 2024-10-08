package com.example.project01.modal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupModal(
    val name: String? = null,
    val imageUrl: String? = null,
    var id: String? = null
): Parcelable


