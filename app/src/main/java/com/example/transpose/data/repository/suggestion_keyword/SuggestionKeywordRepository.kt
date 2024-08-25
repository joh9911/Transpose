package com.example.transpose.data.repository.suggestion_keyword

import okhttp3.ResponseBody

interface SuggestionKeywordRepository {
    suspend fun getSuggestionKeywords(query: String): Result<ResponseBody>
}