//package com.example.transpose.data.repository
//
//import com.example.transpose.utils.Logger
//import org.schabi.newpipe.extractor.NewPipe
//import org.schabi.newpipe.extractor.ServiceList
//import org.schabi.newpipe.extractor.exceptions.ExtractionException
//import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler
//import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory
//import org.schabi.newpipe.extractor.playlist.PlaylistExtractor
//import org.schabi.newpipe.extractor.search.SearchExtractor
//import org.schabi.newpipe.extractor.services.youtube.YoutubeService
//import java.io.IOException
//
//
//class NewPipeService {
//
//    init {
//        NewPipe.init(NewPipeDownloader())
//    }
//
//    private val youtubeService: YoutubeService = ServiceList.YouTube
//
//
//    fun getSearchResult(query: String): VideoPager {
//        try {
//            val extractor: SearchExtractor = youtubeService.getSearchExtractor(query)
//            extractor.fetchPage()
//            return VideoPager(youtubeService, extractor)
//        } catch (e: ExtractionException) {
//            throw NewPipeException("Getting search result for " + query + " fails:" + e.message, e)
//        } catch (e: IOException) {
//            throw NewPipeException("Getting search result for " + query + " fails:" + e.message, e)
//        } catch (e: RuntimeException) {
//            throw NewPipeException("Getting search result for " + query + " fails:" + e.message, e)
//        }
//    }
//
//    fun getPlaylistPager(playlistId: String): PlaylistPager {
//        try {
//            val playlistLinkHandler: ListLinkHandler = getPlaylistHandler(playlistId)
//
//            val playlistExtractor: PlaylistExtractor =
//                youtubeService.getPlaylistExtractor(playlistLinkHandler)
//            playlistExtractor.fetchPage()
//            return PlaylistPager(youtubeService, playlistExtractor)
//        } catch (e: ExtractionException) {
//            throw NewPipeException("Getting playlists for " + playlistId + " fails:" + e.message, e)
//        } catch (e: IOException) {
//            throw NewPipeException("Getting playlists for " + playlistId + " fails:" + e.message, e)
//        } catch (e: java.lang.RuntimeException) {
//            throw NewPipeException("Getting playlists for " + playlistId + " fails:" + e.message, e)
//        }
//    }
//
//    private fun getPlaylistHandler(playlistId: String): ListLinkHandler {
//        val factory: ListLinkHandlerFactory = youtubeService.playlistLHFactory
//        return try {
//            factory.fromUrl(playlistId)
//        } catch (urlParsingException: Exception) {
//            try {
//                factory.fromId(playlistId)
//            } catch (idParsingException: Exception) {
//                throw NewPipeException.ParsingException(
//                    "getPlaylistHandler",
//                    idParsingException
//                )
//            }
//        }
//    }
//
//}