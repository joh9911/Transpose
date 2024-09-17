package com.example.transpose.media.model

import android.net.Uri
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.InfoItem.InfoType
import org.schabi.newpipe.extractor.stream.VideoStream

enum class MediaItemType {
    YOUTUBE,
    LOCAL_FILE
}

data class PlayableItemData(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val type: MediaItemType,

    // YouTube 특정 필드
    val infoType: InfoType? = null,
    val textualUploadDate: String? = null,
    val description: String? = null,
    val viewCount: Long = -1,
    val likeCount: Long = -1,
    val dislikeCount: Long = -1,
    val uploaderUrl: String? = null,
    val uploaderName: String? = null,
    val uploaderSubscriberCount: Long = -1,
    val uploaderAvatars: String? = null,
    val videoStream: VideoStream? = null,
    val relatedItems: MutableList<out InfoItem>? = null,  // 더 구체적인 타입으로 변경 필요

    // 로컬 파일 특정 필드
    val uri: Uri? = null,

    val artist: String? = null,  // 로컬 파일의 "누가 불렀는지" 정보

    // 공통 필드
    val dateAdded: String? = null
)