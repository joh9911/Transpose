package com.example.transpose.data.repository

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist
import org.schabi.newpipe.extractor.stream.Description


object NewPipeUtils {
    fun filterHtml( content: String?): String {
        if (content == null) {
            return ""
        }
        return Jsoup.clean(content, "", Safelist.basic(), Document.OutputSettings().prettyPrint(false))
    }

    fun filterHtml(description: Description): String {
        val result: String
        if (description.getType() === Description.HTML) {
            result = filterHtml(description.getContent())
        } else {
            result = description.getContent()
        }
        return result
    }


    fun getHighestResolutionThumbnail(thumbnailUrl: String?): String? {
        if (thumbnailUrl == null) return null
        val videoId = extractVideoId(thumbnailUrl)
        val resolutions = listOf("maxresdefault", "sddefault", "hqdefault", "mqdefault", "default")


        return "https://i.ytimg.com/vi/$videoId/maxresdefault.jpg"
    }

    private fun extractVideoId(url: String): String {
        // URL에서 비디오 ID 추출
        val regex = "vi/([^/]+)/".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1) ?: ""
    }

    fun removeChannelIdPrefix(channelId: String): String {
        return if (channelId.contains("channel/")) {
            channelId.split("channel/")[1]
        } else {
            channelId
        }
    }
}