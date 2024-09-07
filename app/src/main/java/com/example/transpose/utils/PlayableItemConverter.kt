package com.example.transpose.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.transpose.data.model.local_file.LocalFileData
import com.example.transpose.data.model.newpipe.NewPipeStreamInfoData
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.example.transpose.media.model.MediaItemType
import com.example.transpose.media.model.PlayableItemBasicInfoData
import com.example.transpose.media.model.PlayableItemData
import org.schabi.newpipe.extractor.InfoItem
@RequiresApi(Build.VERSION_CODES.O)

object PlayableItemConverter {

    fun toPlayableItemData(item: Any, playableItemBasicInfoData: PlayableItemBasicInfoData): PlayableItemData{
        return when(item){
            is NewPipeStreamInfoData -> item.toPlayableMediaItem(playableItemBasicInfoData)
            else -> throw IllegalArgumentException("Unsupported type: ${item::class.java.name}")
        }
    }

    fun toBasicInfoData(item: Any): PlayableItemBasicInfoData {
        return when (item) {
            is NewPipeVideoData -> item.toBasicInfoData()
            is LocalFileData -> item.toBasicInfoData()
            is InfoItem -> item.toBasicInfoData()
            else -> throw IllegalArgumentException("Unsupported type: ${item::class.java.name}")
        }
    }

    private fun NewPipeVideoData.toBasicInfoData(): PlayableItemBasicInfoData {
        return PlayableItemBasicInfoData(
            id, title, thumbnailUrl, MediaItemType.YOUTUBE, uploaderName, TextFormatUtil.convertISOToPrettyTime(textualUploadDate)
        )
    }

    private fun LocalFileData.toBasicInfoData(): PlayableItemBasicInfoData {
        return PlayableItemBasicInfoData(
            id.toString(), title, album, MediaItemType.YOUTUBE, artist, TextFormatUtil.formatTimestampToPrettyTime(dateAdded)
        )
    }

    private fun InfoItem.toBasicInfoData(): PlayableItemBasicInfoData {
        return PlayableItemBasicInfoData(
            url, name, thumbnails.first().url, MediaItemType.YOUTUBE, null, null
        )
    }

    fun NewPipeStreamInfoData.toPlayableMediaItem(baseVideoData: PlayableItemBasicInfoData): PlayableItemData {
        return PlayableItemData(
            id = baseVideoData.id,
            title = baseVideoData.title,
            thumbnailUrl = baseVideoData.thumbnailUrl,
            type = MediaItemType.YOUTUBE,
            uploaderName = this.uploaderName ?: baseVideoData.uploaderName,
            textualUploadDate = this.textualUploadDate,
            description = this.description,
            viewCount = this.viewCount,
            likeCount = this.likeCount,
            dislikeCount = this.dislikeCount,
            uploaderUrl = this.uploaderUrl,
            uploaderSubscriberCount = this.uploaderSubscriberCount,
            uploaderAvatars = this.uploaderAvatars,
            videoStream = this.videoStreams?.maxByOrNull { it.getResolution() },
            relatedItems = this.relatedItems
        )
    }
}