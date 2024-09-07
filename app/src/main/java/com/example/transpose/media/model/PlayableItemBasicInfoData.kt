package com.example.transpose.media.model

data class PlayableItemBasicInfoData(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val type: MediaItemType,
    val uploaderName: String?,
    val uploadedDate: String?
)