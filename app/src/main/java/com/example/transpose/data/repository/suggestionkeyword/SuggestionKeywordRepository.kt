package com.example.transpose.data.repository.suggestionkeyword

import okhttp3.ResponseBody

interface SuggestionKeywordRepository {
    suspend fun getSuggestionKeywords(query: String): Result<ResponseBody>
}