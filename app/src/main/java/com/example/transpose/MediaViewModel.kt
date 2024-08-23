package com.example.transpose

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.transpose.data.model.NewPipeContentListData
import com.example.transpose.data.model.NewPipeVideoData
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.service.MediaService
import com.example.transpose.service.MusicServiceHandler
import com.example.transpose.service.audio_effect.AudioEffectHandler
import com.example.transpose.utils.Logger
import com.example.transpose.utils.MediaStateEvents
import com.example.transpose.utils.MusicStates
import com.example.transpose.utils.PlayerUiEvent
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val application: Application,
    private val newPipeRepository: NewPipeRepository,
): AndroidViewModel(application), AudioEffectHandler {
    private val _mediaController = MutableStateFlow<MediaController?>(null)
    val mediaController = _mediaController.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    init {
        initializeMediaController()
    }

    private fun initializeMediaController() {
        Logger.d("initializeMediaController")
        val sessionToken =
            SessionToken(application, ComponentName(application, MediaService::class.java))
        val controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                _mediaController.value = controllerFuture.get()
                Logger.d("${mediaController.value}")
                _mediaController.value?.addListener(playerListener)
                updatePlaybackState()
            },
            MoreExecutors.directExecutor()
        )
    }

    private val _mediaMetaData = MutableStateFlow<MediaMetadata?>(null)
    val mediaMetadata = _mediaMetaData.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            _mediaMetaData.value = mediaMetadata
            super.onMediaMetadataChanged(mediaMetadata)
        }
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            Logger.d("onTracksChanged $tracks")
        }
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Logger.d("onPlayerError $error")
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying

        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updatePlaybackState()
        }
    }

    private fun updatePlaybackState() {
        val controller = _mediaController.value ?: return
        _isPlaying.value = controller.isPlaying
        _duration.value = controller.duration
        _currentPosition.value = controller.currentPosition
    }

    fun setMediaItem(item: NewPipeVideoData) {
        viewModelScope.launch {
            try {
                // 비동기로 URL 가져오기
                val videoUri = getStreamInfoByVideoId(item.id)
                // URL을 성공적으로 가져왔다면 MediaItem 설정
                videoUri?.let { uri ->
                    val mediaItem = MediaItem.Builder()
                        .setUri(uri)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(item.title)
                                .setArtist(item.uploaderName ?: "Unknown Uploader")
                                .setArtworkUri(item.thumbnailUrl?.let { Uri.parse(it) })
                                .setDescription(item.description)
                                .setExtras(Bundle().apply {
                                    putString("viewCount", item.viewCount.toString())
                                    putString("uploaderUrl", item.uploaderUrl)
                                    putBoolean("uploaderVerified", item.uploaderVerified)
                                    putString("streamType", item.streamType.name)
                                    putBoolean("shortFormContent", item.shortFormContent)
                                })
                                .build()
                        )
                        .build()

                    mediaController.value?.let { controller ->
                        controller.setMediaItem(mediaItem)
                        controller.prepare()
                        controller.playWhenReady = true

                        Logger.d("setMediaItem ${controller.mediaItemCount}")
                    }
                }
            } catch (e: Exception) {
                Logger.d("Error setting media item: ${e.message}")
            }
        }
    }

    // URL을 가져오는 함수를 suspend 함수로 변경
    private suspend fun getStreamInfoByVideoId(videoId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val result = newPipeRepository.fetchStreamInfoByVideoId(videoId)
                if (result.isSuccess) {
                    val bestQualityStream = result.getOrNull()?.maxByOrNull { it.getResolution() }
                    bestQualityStream?.content
                } else {
                    Logger.d("getStreamInfoByVideoId ${result.exceptionOrNull()}")
                    null
                }
            } catch (e: Exception) {
                Logger.d("getStreamInfoByVideoId ${e}")
                null
            }
        }
    }

    override fun setPitch(value: Int) {
        TODO("Not yet implemented")
    }

    override fun setTempo(value: Int) {
        TODO("Not yet implemented")
    }

    override fun setBassBoost(value: Int) {
        TODO("Not yet implemented")
    }

    override fun setLoudnessEnhancer(value: Int) {
        TODO("Not yet implemented")
    }

    override fun setEqualizer(value: Int?) {
        TODO("Not yet implemented")
    }

    override fun setVirtualizer(value: Int) {
        TODO("Not yet implemented")
    }

    override fun setPresetReverb(value: Int, sendLevel: Int) {
        TODO("Not yet implemented")
    }

    override fun setEnvironmentalReverb() {
        TODO("Not yet implemented")
    }


}