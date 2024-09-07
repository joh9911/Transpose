package com.example.transpose.data.model.newpipe

import com.example.transpose.media.model.MediaItemType
import com.example.transpose.media.model.PlayableItemData
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.stream.VideoStream

data class NewPipeStreamInfoData(
    val textualUploadDate: String? = null,
    val description: String? = null,
    val viewCount: Long = -1,
    val likeCount: Long = -1,
    val dislikeCount: Long = -1,
    val uploaderUrl: String? = null,
    val uploaderName: String? = null,
    val uploaderSubscriberCount: Long = -1,
    val uploaderAvatars: String? = null,
    val videoStreams: List<VideoStream>? = null,
    val relatedItems: MutableList<out InfoItem>? = null,  // 더 구체적인 타입으로 변경 필요
)