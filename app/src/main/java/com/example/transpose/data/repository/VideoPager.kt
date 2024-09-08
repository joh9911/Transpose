package com.example.transpose.data.repository

import com.example.transpose.data.model.newpipe.NewPipeChannelData
import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.data.model.newpipe.NewPipePlaylistData
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.example.transpose.utils.Logger
import com.myFile.transpose.network.newpipe.Pager
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.ListExtractor
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.channel.ChannelInfoItem
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.exceptions.ParsingException
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory
import java.net.HttpURLConnection
import java.net.URL

open class VideoPager(
    streamingService: StreamingService,
    extractor: ListExtractor<out InfoItem>
) : Pager<InfoItem, NewPipeContentListData>(streamingService, extractor) {

    private val seenVideos = HashSet<String>()

    override fun extract(page: ListExtractor.InfoItemsPage<out InfoItem>): List<NewPipeContentListData> {
        val result = ArrayList<NewPipeContentListData>(page.items.size)
        var repeatCounter = 0
        var unexpected = 0

        for (infoItem in page.items) {
            when (infoItem) {

                is StreamInfoItem -> {
                    val id = getId(streamLinkHandler, infoItem.url)
                    if (seenVideos.contains(id)) {
                        repeatCounter++
                    } else {
                        seenVideos.add(id)
                        result.add(convert(infoItem, id))
                    }
                }
                is PlaylistInfoItem -> {
                    result.add(convert(infoItem, getId(playlistLinkHandler, infoItem.url)))
                }
                is ChannelInfoItem -> {
                    result.add(convert(infoItem))
                }
                else -> {
                    Logger.d("Unexpected item $infoItem, type:${infoItem.javaClass}")
                    unexpected++
                }
            }
        }

        return result
    }

    private fun getId(handler: LinkHandlerFactory, url: String): String {
        return try {
            handler.getId(url)
        } catch (e: ParsingException) {
            throw NewPipeException.ParsingException("getId from VideoPager",e)
        }
    }

    private fun convert(item: StreamInfoItem, id: String): NewPipeContentListData {
        return NewPipeVideoData(
            id = id,
            title = item.name,
            description = item.shortDescription ?: "",
            publishTimestamp = item.uploadDate?.date()?.time?.time,
            thumbnailUrl = NewPipeUtils.getHighestResolutionThumbnail(item.thumbnails.firstOrNull()?.url),
            infoType = item.infoType,
            uploaderName = item.uploaderName,
            uploaderUrl = item.uploaderUrl,
            uploaderAvatars = item.uploaderAvatars,
            uploaderVerified = item.isUploaderVerified,
            duration = item.duration,
            viewCount = item.viewCount,
            textualUploadDate = item.textualUploadDate,
            streamType = item.streamType,
            shortFormContent = item.isShortFormContent
        )
    }

    private fun convert(playlistInfoItem: PlaylistInfoItem, id: String): NewPipeContentListData {
        return NewPipePlaylistData(
            id = id,
            title = playlistInfoItem.name,
            description = playlistInfoItem.description?.content ?: "",
            publishTimestamp = null,  // PlaylistInfoItem doesn't have this information
            thumbnailUrl = NewPipeUtils.getHighestResolutionThumbnail(playlistInfoItem.thumbnails.firstOrNull()?.url),
            infoType = playlistInfoItem.infoType,
            uploaderName = playlistInfoItem.uploaderName ?: "",
            uploaderUrl = playlistInfoItem.uploaderUrl,
            uploaderVerified = playlistInfoItem.isUploaderVerified,
            streamCount = playlistInfoItem.streamCount,
            playlistType = playlistInfoItem.playlistType
        )
    }

    private fun convert(channelInfoItem: ChannelInfoItem): NewPipeContentListData {
        val id = getId(channelInfoItem.url)
        return NewPipeChannelData(
            id = id,
            title = channelInfoItem.name,
            description = channelInfoItem.description ?: "",
            publishTimestamp = null,  // ChannelInfoItem doesn't have this information
            thumbnailUrl = NewPipeUtils.getHighestResolutionThumbnail(channelInfoItem.thumbnails.firstOrNull()?.url),
            infoType = channelInfoItem.infoType,
            subscriberCount = channelInfoItem.subscriberCount,
            streamCount = channelInfoItem.streamCount,
            verified = channelInfoItem.isVerified
        )
    }

    private fun getId(url: String): String {
        return try {
            channelLinkHandler.getId(url)
        } catch (e: ParsingException) {
            Logger.e("Unable to parse channel url $url", e)
            url
        }
    }



}