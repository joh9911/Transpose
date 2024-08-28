package com.example.transpose.media

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy

@UnstableApi
class CustomMediaSourceFactory(
    private val context: Context
) : MediaSource.Factory {
    private val dataSourceFactory = DefaultDataSource.Factory(context)
    private var drmSessionManagerProvider: DrmSessionManagerProvider? = null
    private var loadErrorHandlingPolicy: LoadErrorHandlingPolicy? = null
    override fun setDrmSessionManagerProvider(drmSessionManagerProvider: DrmSessionManagerProvider): MediaSource.Factory {
        this.drmSessionManagerProvider = drmSessionManagerProvider

        return this
    }

    override fun setLoadErrorHandlingPolicy(loadErrorHandlingPolicy: LoadErrorHandlingPolicy): MediaSource.Factory {
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy

        return this
    }

    override fun getSupportedTypes(): IntArray {
        return intArrayOf(C.TYPE_OTHER)

    }

    @OptIn(UnstableApi::class)
    override fun createMediaSource(mediaItem: MediaItem): MediaSource {
        val extras = mediaItem.playbackProperties?.tag as? Bundle
        val videoUri = extras?.getString("videoUrl")?.let { Uri.parse(it) }
        val audioUri = extras?.getString("audioUrl")?.let { Uri.parse(it) }

        return if (videoUri != null && audioUri != null) {
            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUri))
            val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(audioUri))
            MergingMediaSource(videoSource, audioSource)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }
    }
}