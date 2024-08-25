package com.example.transpose.data.model.newpipe

data class NewPipeChannelData(
    override val id: String,
    override val title: String,
    override val description: String,
    override val publishTimestamp: Long?,
    override val thumbnailUrl: String?,
    val subscriberCount: Long,
    val streamCount: Long,
    val verified: Boolean
): NewPipeContentListData