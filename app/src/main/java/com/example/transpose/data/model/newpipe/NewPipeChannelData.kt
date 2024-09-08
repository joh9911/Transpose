package com.example.transpose.data.model.newpipe

import org.schabi.newpipe.extractor.InfoItem

data class NewPipeChannelData(
    override val id: String,
    override val title: String,
    override val description: String,
    override val publishTimestamp: Long?,
    override val thumbnailUrl: String?,
    override val infoType: InfoItem.InfoType,
    val subscriberCount: Long,
    val streamCount: Long,
    val verified: Boolean
): NewPipeContentListData