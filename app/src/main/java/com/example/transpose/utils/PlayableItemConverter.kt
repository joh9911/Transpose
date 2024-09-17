package com.example.transpose.utils

import android.os.Bundle
import com.example.transpose.data.database.entity.VideoEntity
import com.example.transpose.data.model.local_file.LocalFileData
import com.example.transpose.data.model.newpipe.NewPipeStreamInfoData
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.example.transpose.data.repository.NewPipeUtils
import com.example.transpose.media.model.MediaItemType
import com.example.transpose.media.model.PlayableItemBasicInfoData
import com.example.transpose.media.model.PlayableItemData
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.InfoItem.InfoType
import org.schabi.newpipe.extractor.stream.StreamType


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
            is VideoEntity -> item.toBasicInfoData()
            else -> throw IllegalArgumentException("Unsupported type: ${item::class.java.name}")
        }
    }

    private fun NewPipeVideoData.toBasicInfoData(): PlayableItemBasicInfoData {
        return try {
            PlayableItemBasicInfoData(
                id = id,
                title = title,
                thumbnailUrl = thumbnailUrl,
                type = MediaItemType.YOUTUBE,
                infoType = infoType,
                uploaderName = uploaderName,
                uploadedDate = TextFormatUtil.formatTimestampToPrettyTime(publishTimestamp)
            )
        } catch (e: Exception) {
            Logger.d("Error in NewPipeVideoData.toBasicInfoData: ${e.message}")
            throw IllegalArgumentException("Failed to convert NewPipeVideoData: ${e.message}", e)
        }
    }

    private fun LocalFileData.toBasicInfoData(): PlayableItemBasicInfoData {
        return try {
            PlayableItemBasicInfoData(
                id = uri.toString(),
                title = title,
                thumbnailUrl = album,  // Note: Is this correct? album as thumbnailUrl?
                type = MediaItemType.LOCAL_FILE,  // Changed from YOUTUBE to LOCAL_FILE
                infoType = null,
                uploaderName = artist,
                uploadedDate = TextFormatUtil.formatTimestampToPrettyTime(dateAdded)
            )
        } catch (e: Exception) {
            Logger.d("Error in LocalFileData.toBasicInfoData: ${e.message}")
            throw IllegalArgumentException("Failed to convert LocalFileData: ${e.message}", e)
        }
    }

    private fun InfoItem.toBasicInfoData(): PlayableItemBasicInfoData {
        return try {
            PlayableItemBasicInfoData(
                id = url,
                title = name,
                thumbnailUrl = NewPipeUtils.getHighestResolutionThumbnail(thumbnails.firstOrNull()?.url),
                type = MediaItemType.YOUTUBE,
                infoType = infoType,
                uploaderName = null,
                uploadedDate = null
            )
        } catch (e: Exception) {
            Logger.d("Error in InfoItem.toBasicInfoData: ${e.message}")
            throw IllegalArgumentException("Failed to convert InfoItem: ${e.message}", e)
        }
    }

    private fun VideoEntity.toBasicInfoData(): PlayableItemBasicInfoData {
        return try {
            PlayableItemBasicInfoData(
                id = this.id,
                title = this.title,
                thumbnailUrl = NewPipeUtils.getHighestResolutionThumbnail(this.thumbnailUrl),
                type = MediaItemType.YOUTUBE,
                infoType = InfoType.STREAM,
                uploaderName = this.uploaderName,
                uploadedDate = this.textualUploadDate
            )
        }catch (e: Exception) {
            Logger.d("Error in VideoEntity.toBasicInfoData(): ${e.message}")
            throw IllegalArgumentException("Failed to convert InfoItem: ${e.message}", e)
        }

    }

    fun NewPipeStreamInfoData.toPlayableMediaItem(baseVideoData: PlayableItemBasicInfoData): PlayableItemData {
        Logger.d("NewPipeStreamInfoData.toPlayableMediaItem ${baseVideoData.uploadedDate}")
        return try {
            PlayableItemData(
                id = baseVideoData.id,
                title = baseVideoData.title,
                thumbnailUrl = baseVideoData.thumbnailUrl,
                type = MediaItemType.YOUTUBE,
                infoType = baseVideoData.infoType,
                uploaderName = this.uploaderName ?: baseVideoData.uploaderName,
                textualUploadDate = TextFormatUtil.convertISOToPrettyTime(this.textualUploadDate),
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
        }catch (e: Exception){
            Logger.d("toPlayableMediaItem $e")
            throw IllegalArgumentException("Failed to convert toPlayableMediaItem: ${e.message}", e)
        }
    }

    fun PlayableItemBasicInfoData.toBundle(): Bundle {
        return Bundle().apply {
            putString("id", id)
            putString("title", title)
            putString("thumbnailUrl", thumbnailUrl)
            putString("type", type.name)
            putString("infoType", infoType?.name)
            putString("uploaderName", uploaderName)
            putString("uploadedDate", uploadedDate)
        }
    }

    fun Bundle.toPlayableItemBasicInfoData(): PlayableItemBasicInfoData {
        return PlayableItemBasicInfoData(
            id = getString("id") ?: "",
            title = getString("title") ?: "",
            thumbnailUrl = getString("thumbnailUrl"),
            type = MediaItemType.valueOf(getString("type") ?: MediaItemType.YOUTUBE.name),
            infoType = getString("infoType")?.let { InfoItem.InfoType.valueOf(it) },
            uploaderName = getString("uploaderName"),
            uploadedDate = getString("uploadedDate")
        )
    }

    fun PlayableItemData.toNewPipeVideoData(): NewPipeVideoData {
        return NewPipeVideoData(
            id = id,
            title = title,
            description = description ?: "",
            publishTimestamp = null,
            thumbnailUrl = thumbnailUrl,
            infoType = infoType ?: InfoItem.InfoType.STREAM,
            uploaderName = uploaderName,
            uploaderUrl = uploaderUrl,
            uploaderAvatars = null,
            uploaderVerified = false, // PlayableItemData에는 이 정보가 없으므로 기본값으로 false 설정
            duration = -1, // PlayableItemData에는 duration 정보가 없으므로 -1로 설정
            viewCount = viewCount,
            textualUploadDate = textualUploadDate,
            streamType = StreamType.VIDEO_STREAM, // 기본값으로 VIDEO_STREAM 설정
            shortFormContent = false // PlayableItemData에는 이 정보가 없으므로 기본값으로 false 설정
        )
    }


}