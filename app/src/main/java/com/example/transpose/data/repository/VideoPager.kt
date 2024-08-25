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
            thumbnailUrl = item.thumbnails.firstOrNull()?.url,
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
            thumbnailUrl = playlistInfoItem.thumbnails.firstOrNull()?.url,
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
            thumbnailUrl = channelInfoItem.thumbnails.firstOrNull()?.url,
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

    private fun getHighestResolutionThumbnail(thumbnailUrl: String?): String? {
        if (thumbnailUrl == null) return null
        val videoId = extractVideoId(thumbnailUrl)
        val resolutions = listOf("maxresdefault", "sddefault", "hqdefault", "mqdefault", "default")

        for (resolution in resolutions) {
            val url = "https://i.ytimg.com/vi/$videoId/$resolution.jpg"
            if (doesImageExist(url)) {
                return url
            }
        }

        // 모든 해상도를 시도해도 실패하면 원본 URL 반환
        return thumbnailUrl
    }

    private fun extractVideoId(url: String): String {
        // URL에서 비디오 ID 추출
        val regex = "vi/([^/]+)/".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1) ?: ""
    }

    private fun doesImageExist(urlString: String): Boolean {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            false
        }
    }
}