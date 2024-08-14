package com.example.transpose.data.repository

import okhttp3.OkHttpClient
import okhttp3.Request
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Response
import java.util.concurrent.TimeUnit

class NewPipeDownloader : Downloader() {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun execute(request: org.schabi.newpipe.extractor.downloader.Request): Response {
        val httpRequest = Request.Builder().url(request.url())

        // Add headers
        for ((key, values) in request.headers()) {
            for (value in values) {
                httpRequest.addHeader(key, value)
            }
        }

        // Set method and body if necessary
        when (request.httpMethod()) {
            "GET" -> httpRequest.get()
            "POST" -> request.dataToSend()?.let {
                httpRequest.post(okhttp3.RequestBody.create(null, it))
            }
            "HEAD" -> httpRequest.head()
            else -> throw IllegalArgumentException("Unsupported method: ${request.httpMethod()}")
        }

        val response = client.newCall(httpRequest.build()).execute()

        return Response(
            response.code,
            response.message,
            response.headers.toMultimap(),
            response.body?.string(),
            response.request.url.toString()
        )
    }
}