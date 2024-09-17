package com.myFile.transpose.network.newpipe

import com.example.transpose.data.repository.NewPipeException
import okio.IOException
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.ListExtractor
import org.schabi.newpipe.extractor.Page
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory

abstract class Pager<I : InfoItem, O>(
    protected val streamingService: StreamingService,
    private val extractor: ListExtractor<out InfoItem>
){
    private var nextPage: Page? = null
    private var hasNextPage = true
    private var lastException: Exception? = null

    protected val streamLinkHandler: LinkHandlerFactory = streamingService.streamLHFactory
    protected val playlistLinkHandler: ListLinkHandlerFactory = streamingService.playlistLHFactory
    protected val channelLinkHandler: LinkHandlerFactory = streamingService.channelLHFactory

    fun isHasNextPage(): Boolean = hasNextPage


    open fun getNextPage(): List<O> {
        if (!hasNextPage || extractor == null) {
            return emptyList()
        }
        return try {
            if (nextPage == null) {
                extractor.fetchPage()
                process(extractor.initialPage)
            } else {
                process(extractor.getPage(nextPage))
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> throw NewPipeException.PageCannotBeLoaded("getNextPage from Pager}",e)
                is ExtractionException -> throw NewPipeException.ExtractionFailed("getNextPage from Pager",e)
                else -> throw NewPipeException.UnknownError("getNextPage from Pager",e)
            }
        }
    }

    fun getPageAndExtract(page: Page): List<O> {
        return try {
            extract(extractor.getPage(page))
        } catch (e: Exception) {
            when (e) {
                is IOException -> throw NewPipeException.PageCannotBeLoaded("getNextPage from Pager",e)
                is ExtractionException -> throw NewPipeException.ExtractionFailed("getNextPage from Pager",e)
                else -> throw NewPipeException.UnknownError("getNextPage from Pager",e)
            }
        }
    }


    protected open fun process(page: ListExtractor.InfoItemsPage<out InfoItem>): List<O> {
        nextPage = page.nextPage
        hasNextPage = page.hasNextPage()
        return extract(page)
    }

    fun getNextPageInfo(): Page? = nextPage

    protected abstract fun extract(page: ListExtractor.InfoItemsPage<out InfoItem>): List<O>

//    protected fun getHighQualityThumbnailUrl(originalUrl: String?): String? {
//        originalUrl ?: return null
//
//        // YouTube 비디오 ID 추출
//        val videoId = originalUrl.substringAfterLast("/").substringBefore(".")
//
//        // 고화질 썸네일 URL 생성 (maxresdefault.jpg 사용)
//        return "https://i.ytimg.com/vi/$videoId/maxresdefault.jpg"
//    }
//
//    // 다양한 해상도의 썸네일을 시도하는 함수
//    protected fun getBestThumbnailUrl(originalUrl: String?): String? {
//        originalUrl ?: return null
//
//        val videoId = originalUrl.substringAfterLast("/").substringBefore(".")
//        val resolutions = listOf("maxresdefault", "sddefault", "hqdefault", "mqdefault", "default")
//
//        for (resolution in resolutions) {
//            val url = "https://i.ytimg.com/vi/$videoId/$resolution.jpg"
//            if (urlExists(url)) {
//                return url
//            }
//        }
//
//        return originalUrl // 모든 해상도를 시도해도 실패하면 원본 URL 반환
//    }
}