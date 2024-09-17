package com.example.transpose.data.repository


import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.data.model.newpipe.NewPipePlaylistData
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.ListExtractor.InfoItemsPage
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import org.schabi.newpipe.extractor.exceptions.ParsingException
import org.schabi.newpipe.extractor.playlist.PlaylistExtractor

class PlaylistPager(
    streamingService: StreamingService,
    private val playlistExtractor: PlaylistExtractor
) : VideoPager(streamingService, playlistExtractor) {

    private var playlist: NewPipePlaylistData? = null

    override fun extract(page: InfoItemsPage<out InfoItem>): List<NewPipeContentListData> {
//        if (playlist == null) {
//            try {
//                val uploaderUrl = playlistExtractor.uploaderUrl
//                Logger.d("extract 실행 $uploaderUrl ${playlistExtractor.id}")
//                val channelId = uploaderUrl?.let { streamingService.channelLHFactory.fromUrl(it).id }
//                playlist = NewPipePlaylistData(
//                    id = playlistExtractor.id,
//                    title = playlistExtractor.name,
//                    description = "",  // You might want to get this from playlistExtractor if available
//                    publishTimestamp = null,  // You might want to get this from playlistExtractor if available
//                    thumbnailUrl = playlistExtractor.thumbnails.firstOrNull()?.url,
//                    uploaderName = playlistExtractor.uploaderName,
//                    uploaderUrl = uploaderUrl,
//                    uploaderVerified = false,  // You might want to get this from playlistExtractor if available
//                    streamCount = playlistExtractor.streamCount.toLong(),
//                    playlistType = null  // You might want to set this if available
//                )
//            } catch (e: ParsingException) {
//                Logger.d("오류")
//                throw NewPipeException("Error parsing playlist data", e)
//            }
//        }
        return super.extract(page)
    }


    fun getPlaylist(): NewPipePlaylistData? {
        try {
            val uploaderUrl = playlistExtractor.uploaderUrl
            val channelId = uploaderUrl?.let { streamingService.channelLHFactory.fromUrl(it).id }
            playlist = NewPipePlaylistData(
                id = playlistExtractor.id,
                title = playlistExtractor.name,
                description = playlistExtractor.description.content,
                publishTimestamp = null,
                thumbnailUrl = NewPipeUtils.getHighestResolutionThumbnail(playlistExtractor.thumbnails.firstOrNull()?.url),
                infoType = InfoItem.InfoType.PLAYLIST,
                uploaderName = playlistExtractor.uploaderName,
                uploaderUrl = uploaderUrl,
                uploaderVerified = false,  // You might want to get this from playlistExtractor if available
                streamCount = playlistExtractor.streamCount.toLong(),
                playlistType = null  // You might want to set this if available
            )
        } catch (e: Exception) {
            when (e) {
                is ParsingException -> throw NewPipeException.ParsingException("getPlaylist from PlaylistPager",e)
                is ExtractionException -> throw NewPipeException.ExtractionFailed("getPlaylist from PlaylistPager",e)
                else -> throw NewPipeException.UnknownError("getPlaylist from PlaylistPager",e)
            }
        }
        return playlist
    }
}