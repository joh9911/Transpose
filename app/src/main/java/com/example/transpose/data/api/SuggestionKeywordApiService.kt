package com.example.transpose.data.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface SuggestionKeywordApiService {
    @GET("search")
    suspend fun getSuggestionKeyword(@Query("client") client: String,
                                     @Query("ds") ds: String,
                                     @Query("q") q: String
    ): ResponseBody
}