package com.example.transpose.data.model

import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.stream.StreamType

data class NewPipeVideoData(
    override val id: String,
    override val title: String,
    override val description: String,
    override val publishTimestamp: Long?,
    override val thumbnailUrl: String?,
    val uploaderName: String?,
    val uploaderUrl: String?,
    val uploaderAvatars: List<Image>,
    val uploaderVerified: Boolean,
    val duration: Long,
    val viewCount: Long,
    val textualUploadDate: String?,
    val streamType: StreamType,
    val shortFormContent: Boolean
): NewPipeContentListData