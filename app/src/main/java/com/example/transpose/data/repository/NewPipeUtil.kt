package com.example.transpose.data.repository

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.InfoItem
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


    fun getThumbnailUrl(images: List<Image?>): String? {
        return images.filterNotNull()
            .maxByOrNull { it.width }
            ?.url
    }


    fun getThumbnailUrl(comment: InfoItem): String? {
        return getThumbnailUrl(comment.thumbnails)
    }
}