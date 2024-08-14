package com.example.transpose.data.repository.newpipe

import com.example.transpose.data.model.NewPipeContentListData
import com.example.transpose.data.model.NewPipePlaylistData
import com.example.transpose.data.repository.NewPipeDownloader
import com.example.transpose.data.repository.NewPipeException
import com.example.transpose.data.repository.PlaylistPager
import com.example.transpose.data.repository.VideoPager
import com.example.transpose.utils.Logger
import org.schabi.newpipe.extractor.Extractor
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.channel.ChannelExtractor
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import org.schabi.newpipe.extractor.exceptions.ParsingException
import org.schabi.newpipe.extractor.linkhandler.LinkHandler
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory
import org.schabi.newpipe.extractor.playlist.PlaylistExtractor
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.search.SearchExtractor
import org.schabi.newpipe.extractor.services.youtube.YoutubeService
import java.util.LinkedList
import javax.inject.Inject

class NewPipeRepositoryImpl @Inject constructor(): NewPipeRepository {
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

    override suspend fun fetchPlaylistData(playlistId: String): Result<NewPipePlaylistData?> {
        try {
            val playlistLinkHandler: ListLinkHandler = getPlaylistHandler(playlistId)

            val playlistExtractor: PlaylistExtractor =
                getPlaylistExtractor(playlistLinkHandler)

            fetchPageInExtractor(playlistExtractor)
            val pager = PlaylistPager(youtubeService, playlistExtractor)
            return Result.success(getPlaylistInPager(pager))
        }catch (e: Exception){
            return Result.failure(e)
        }

    }

    override suspend fun fetchPlaylistItemData(playlistPager: PlaylistPager): Result<List<NewPipeContentListData>> {
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



    override suspend fun fetchSearchResult(query: String): Result<List<NewPipeContentListData>> {
        return try {
            val extractor = getSearchExtractor(query)
            fetchPageInExtractor(extractor)
            val videoPager = VideoPager(youtubeService, extractor)
            Result.success(videoPager.getNextPage())
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






}