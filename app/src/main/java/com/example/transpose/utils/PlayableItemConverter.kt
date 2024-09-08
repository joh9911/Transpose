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
import org.schabi.newpipe.extractor.InfoItem.InfoType

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
                id = id.toString(),
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
                thumbnailUrl = thumbnails.firstOrNull()?.url,
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

    fun NewPipeStreamInfoData.toPlayableMediaItem(baseVideoData: PlayableItemBasicInfoData): PlayableItemData {
        return try {
            PlayableItemData(
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
        }catch (e: Exception){
            Logger.d("toPlayableMediaItem $e")
            throw IllegalArgumentException("Failed to convert toPlayableMediaItem: ${e.message}", e)
        }
    }
}