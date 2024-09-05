package com.example.transpose.utils

import java.text.DecimalFormat
import java.util.Locale

enum class CountUnit(val value: Long, val koreanIndex: Int, val otherIndex: Int) {
    THOUSAND(1_000, 1, 1),
    TEN_THOUSAND(10_000, 2, 1),
    HUNDRED_THOUSAND(100_000, 2, 1),
    MILLION(1_000_000, 2, 3),
    HUNDRED_MILLION(100_000_000, 4, 3),
    BILLION(1_000_000_000, 4, 5)
}

object CountFormatter {
    private val df = DecimalFormat("#.#")

    fun format(count: Long, stringArray: Array<String>, isSubscriber: Boolean = false): String {
        val isKorean = Locale.getDefault().language == "ko"
        val unit = when {
            count < CountUnit.THOUSAND.value -> return String.format(stringArray[0], count)
            count < CountUnit.TEN_THOUSAND.value -> CountUnit.THOUSAND
            count < CountUnit.HUNDRED_THOUSAND.value -> if (isKorean) CountUnit.TEN_THOUSAND else CountUnit.THOUSAND
            count < CountUnit.MILLION.value -> if (isKorean) CountUnit.TEN_THOUSAND else CountUnit.THOUSAND
            count < CountUnit.HUNDRED_MILLION.value -> if (isKorean) CountUnit.TEN_THOUSAND else CountUnit.MILLION
            count < CountUnit.BILLION.value -> if (isKorean) CountUnit.HUNDRED_MILLION else CountUnit.MILLION
            else -> if (isKorean) CountUnit.HUNDRED_MILLION else CountUnit.BILLION
        }

        val convertedCount = if (isKorean && unit == CountUnit.TEN_THOUSAND && count >= CountUnit.HUNDRED_THOUSAND.value) {
            (count / unit.value).toString()
        } else {
            df.format(count.toDouble() / unit.value)
        }

        val index = if (isKorean) unit.koreanIndex else unit.otherIndex
        return String.format(stringArray[index], convertedCount)
    }
}