package com.example.transpose.service.mediacontroller

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.StateFlow

interface MediaControllerManager {
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val currentMediaItem: StateFlow<MediaItem?>

    fun initializeController()
    fun releaseController()
    fun play()
    fun pause()
    fun seekTo(position: Long)
    fun skipToNext()
    fun skipToPrevious()
    fun setMediaItem(mediaItem: MediaItem)
    fun addMediaItem(mediaItem: MediaItem)
    fun addMediaItems(mediaItems: List<MediaItem>)
    fun removeMediaItem(index: Int)
    fun clearMediaItems()
    fun setPlaybackSpeed(speed: Float)
    fun setRepeatMode(repeatMode: Int)
    fun setShuffleModeEnabled(shuffleModeEnabled: Boolean)
}