package com.example.transpose.media.model

import org.schabi.newpipe.extractor.InfoItem.InfoType

data class PlayableItemBasicInfoData(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val type: MediaItemType,
    val infoType: InfoType?,
    val uploaderName: String?,
    val uploadedDate: String?
)