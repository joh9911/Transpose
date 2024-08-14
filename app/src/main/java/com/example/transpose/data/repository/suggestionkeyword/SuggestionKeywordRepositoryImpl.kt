package com.example.transpose.data.repository.suggestionkeyword

import com.example.transpose.data.api.SuggestionKeywordApiService
import okhttp3.ResponseBody
import javax.inject.Inject


class SuggestionKeywordRepositoryImpl @Inject constructor(
    private val suggestionKeywordApiService: SuggestionKeywordApiService
) : SuggestionKeywordRepository {


    override suspend fun getSuggestionKeywords(query: String): Result<ResponseBody> = runCatching {
        suggestionKeywordApiService.getSuggestionKeyword("firefox", "yt", query)
    }
}