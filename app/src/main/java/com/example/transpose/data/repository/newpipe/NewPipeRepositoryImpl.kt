package com.example.transpose.data.repository.newpipe

import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.data.model.newpipe.NewPipePlaylistData
import com.example.transpose.data.repository.NewPipeException
import com.example.transpose.data.repository.PlaylistPager
import com.example.transpose.data.repository.VideoPager
import org.schabi.newpipe.extractor.Extractor
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.channel.ChannelExtractor
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import org.schabi.newpipe.extractor.exceptions.ParsingException
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory
import org.schabi.newpipe.extractor.playlist.PlaylistExtractor
import org.schabi.newpipe.extractor.search.SearchExtractor
import org.schabi.newpipe.extractor.services.youtube.YoutubeService
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.StreamExtractor
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.VideoStream
import javax.inject.Inject

class NewPipeRepositoryImpl @Inject constructor(

): NewPipeRepository {
    private val youtubeService: YoutubeService

    init {
        NewPipe.init(NewPipeDownloader())
        youtubeService = ServiceList.YouTube
    }

    private fun getPlaylistHandler(playlistId: String): ListLinkHandler {
        val factory: ListLinkHandlerFactory = youtubeService.playlistLHFactory
        return try {
            factory.fromUrl(playlistId)
        } catch (urlParsingException: Exception) {
            try {
                factory.fromId(playlistId)
            } catch (idParsingException: Exception) {
                throw NewPipeException.ParsingException(
                    "getPlaylistHandler",
                    idParsingException
                )
            }
        }
    }

    private fun getPlaylistExtractor(linkHandler: ListLinkHandler): PlaylistExtractor {
        return youtubeService.getPlaylistExtractor(linkHandler)
    }

    private fun <T : Extractor> fetchPageInExtractor(extractor: T){
        try {
            extractor.fetchPage()
        } catch (e: Exception) {
            throw NewPipeException.NetworkError("fetchPageInExtractor", e)
        } catch (e: ExtractionException) {
            throw NewPipeException.ExtractionFailed("fetchPageInExtractor", e)
        } catch (e: Exception) {
            throw NewPipeException.UnknownError("fetchPageInExtractor", e)
        }
    }


    private fun getPlaylistInPager(pager: PlaylistPager): NewPipePlaylistData? {
        return pager.getPlaylist()
    }

    override suspend fun createPlaylistPager(playlistId: String): PlaylistPager {
        return try {
            val linkHandler = getPlaylistHandler(playlistId)
            val extractor = getPlaylistExtractor(linkHandler)
            fetchPageInExtractor(extractor)
            PlaylistPager(youtubeService, extractor)
        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun fetchPlaylistResult(pager: PlaylistPager): Result<NewPipePlaylistData?> {
        return try {
            Result.success(getPlaylistInPager(pager))
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun fetchPlaylistItemsResult(playlistPager: PlaylistPager): Result<List<NewPipeContentListData>> {
        return try {
            Result.success(playlistPager.getNextPage())
        }catch (e: Exception){
            return Result.failure(e)
        }
    }


    private fun getSearchExtractor(query: String): SearchExtractor{
        return try {
            youtubeService.getSearchExtractor(query)
        }catch (e: ExtractionException){
            throw NewPipeException.ExtractionFailed("getSearchExtractor",e)
        }
    }

    override suspend fun createSearchPager(query: String): VideoPager {
        return try {
            val extractor = getSearchExtractor(query)
            fetchPageInExtractor(extractor)
            VideoPager(youtubeService, extractor)
        }catch (e: Exception){
            throw e
        }
    }


    override suspend fun fetchSearchResult(pager: VideoPager): Result<List<NewPipeContentListData>> {
        return try {
            Result.success(pager.getNextPage())
        }catch (e: Exception){
            Result.failure(e)
        }
    }




    private fun getChannelLinkHandler(channelId: String): ListLinkHandler{
        val factory: ListLinkHandlerFactory = youtubeService.channelLHFactory

        return try {
            factory.fromUrl(channelId)
        } catch (urlParsingException: Exception) {
            try {
                factory.fromId(channelId)
            } catch (idParsingException: Exception) {
                throw NewPipeException.ParsingException(
                    "getChannelLinkHandler",
                    idParsingException
                )
            }
        }
    }

    private fun getChannelExtractor(linkHandler: ListLinkHandler): ChannelExtractor{
        return youtubeService.getChannelExtractor(linkHandler)
    }

    private fun getChannelTabExtractor(linkHandler: ListLinkHandler): ChannelTabExtractor{
        return youtubeService.getChannelTabExtractor(linkHandler)
    }


    override suspend fun fetchPlaylistWithChannelId(channelId: String): Result<List<NewPipeContentListData>?> {
        try {
            val channelLinkHandler = getChannelLinkHandler(channelId)

            val channelExtractor = getChannelExtractor(channelLinkHandler)

            fetchPageInExtractor(channelExtractor)

            val playlistsTabLinkHandler = channelExtractor.tabs.find { it.contentFilters.contains("playlists") }

            if (playlistsTabLinkHandler != null) {
                val channelTabExtractor = getChannelTabExtractor(playlistsTabLinkHandler)
                val pager = VideoPager(youtubeService, channelTabExtractor)

                return Result.success(pager.getNextPage())
            }
            return Result.failure(Exception("No playlist in that channel Id"))

        }catch (e: Exception){
            return Result.failure(e)
        }
    }

    private fun getStreamExtractor(videoId: String): StreamExtractor {
        return try {
            youtubeService.getStreamExtractor(videoId)
        }catch (e: Exception){
            throw NewPipeException.ExtractionFailed("getStreamExtractor", e)
        }
    }


    private fun getVideoUrl(videoId: String): String {
        return try {
            youtubeService.streamLHFactory.getUrl(videoId)
        }
        catch (e: Exception){
            when(e){
                is ParsingException -> throw NewPipeException.ParsingException("getVideoUrl",e)
                is UnsupportedOperationException -> throw NewPipeException.UnsupportedOperationException("getVideoUrl",e)
                else -> throw NewPipeException.UnknownError("getVideoUrl",e)
            }
        }
    }

    override suspend fun fetchSeparatedStreamByVideoId(videoId: String): Result<Pair<MutableList<VideoStream>?, MutableList<AudioStream>>> {
        return try {
            val extractor = getStreamExtractor(getVideoUrl(videoId))
            extractor.fetchPage()
            Result.success(Pair(extractor.videoOnlyStreams, extractor.audioStreams))
        }catch (e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun fetchVideoStreamByVideoId(videoId: String): Result<MutableList<VideoStream>>{
        return try {
            val extractor = getStreamExtractor(getVideoUrl(videoId))
            extractor.fetchPage()
            Result.success(extractor.videoStreams)
        }catch (e: Exception){
            return Result.failure(e)
        }
    }


}