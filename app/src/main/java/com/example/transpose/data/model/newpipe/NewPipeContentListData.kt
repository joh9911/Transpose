package com.example.transpose.data.model.newpipe

import org.schabi.newpipe.extractor.InfoItem.InfoType

interface NewPipeContentListData {
    val id: String
    val title: String
    val description: String
    val publishTimestamp: Long?
    val thumbnailUrl: String?
    val infoType: InfoType
}