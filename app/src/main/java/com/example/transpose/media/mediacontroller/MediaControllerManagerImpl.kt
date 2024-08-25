package com.example.transpose.media.mediacontroller

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.transpose.media.MediaService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControllerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaControllerManager {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration

    private val _currentMediaItem = MutableStateFlow<MediaItem?>(null)
    override val currentMediaItem: StateFlow<MediaItem?> = _currentMediaItem

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            _currentPosition.value = newPosition.positionMs
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _currentMediaItem.value = mediaItem
            _duration.value = mediaController?.duration ?: 0L
        }
    }

    override fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, MediaService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            mediaController?.addListener(playerListener)
        }, MoreExecutors.directExecutor())
    }

    override fun releaseController() {
        mediaController?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
        controllerFuture = null
    }

    override fun play() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun skipToNext() {
        mediaController?.seekToNextMediaItem()
    }

    override fun skipToPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        mediaController?.setMediaItem(mediaItem)
    }

    override fun addMediaItem(mediaItem: MediaItem) {
        mediaController?.addMediaItem(mediaItem)
    }

    override fun addMediaItems(mediaItems: List<MediaItem>) {
        mediaController?.addMediaItems(mediaItems)
    }

    override fun removeMediaItem(index: Int) {
        mediaController?.removeMediaItem(index)
    }

    override fun clearMediaItems() {
        mediaController?.clearMediaItems()
    }

    override fun setPlaybackSpeed(speed: Float) {
        mediaController?.setPlaybackSpeed(speed)
    }

    override fun setRepeatMode(repeatMode: Int) {
        mediaController?.repeatMode = repeatMode
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        mediaController?.shuffleModeEnabled = shuffleModeEnabled
    }
}