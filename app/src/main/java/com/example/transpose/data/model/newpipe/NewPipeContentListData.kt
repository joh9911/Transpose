package com.example.transpose.data.model.newpipe

import org.ocpsoft.prettytime.PrettyTime
import java.util.*

interface NewPipeContentListData {
    val id: String
    val title: String
    val description: String
    val publishTimestamp: Long?
    val thumbnailUrl: String?

    fun getPublishDatePretty(locale: Locale = Locale.getDefault()): String {
        val prettyTime = PrettyTime(locale)
        return publishTimestamp?.let { timestamp ->
            prettyTime.format(Date(timestamp))
        } ?: "Unknown"
    }

    fun getPublishDatePrettyKorean(): String = getPublishDatePretty(Locale.KOREAN)

    fun getPublishDatePrettyEnglish(): String = getPublishDatePretty(Locale.ENGLISH)

    fun getCustomPublishDatePretty(
        locale: Locale = Locale.getDefault(),
        recentThreshold: Long = 7 * 24 * 60 * 60 * 1000 // 7 days in milliseconds
    ): String {
        val now = System.currentTimeMillis()
        return publishTimestamp?.let { timestamp ->
            when {
                now - timestamp < recentThreshold -> getPublishDatePretty(locale)
                else -> {
                    val dateFormat = when (locale.language) {
                        "ko" -> "yyyy년 MM월 dd일"
                        else -> "MMM dd, yyyy"
                    }
                    java.text.SimpleDateFormat(dateFormat, locale).format(Date(timestamp))
                }
            }
        } ?: "Unknown"
    }
}