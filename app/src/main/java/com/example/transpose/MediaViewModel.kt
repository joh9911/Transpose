package com.example.transpose

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.media.MediaService
import com.example.transpose.media.audio_effect.data.equalizer.EqualizerPresets
import com.example.transpose.media.audio_effect.data.equalizer.EqualizerSettings
import com.example.transpose.media.audio_effect.data.reverb.ReverbPresets
import com.example.transpose.utils.Logger
import com.example.transpose.utils.constants.MediaSessionCallback
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log2
import kotlin.math.roundToInt

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val application: Application,
    private val newPipeRepository: NewPipeRepository,
): AndroidViewModel(application) {
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

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            super.onPlaybackParametersChanged(playbackParameters)

            val currentPitch = playbackParameters.pitch

            val semitonesPitch = 12 * log2(currentPitch.toDouble())

            val pitchValue = ((semitonesPitch * 10) + 100).roundToInt().coerceIn(0, 200)
            _pitchValue.value = pitchValue

            val currentTempo = playbackParameters.speed

            val semitonesTempo = 12 * log2(currentTempo.toDouble())

            val tempoValue = ((semitonesTempo * 10) + 100).roundToInt().coerceIn(0, 200)
            _tempoValue.value = tempoValue

        }

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
                val videoUri = getStreamInfoByVideoId(item.id)
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

    private val _pitchValue = MutableStateFlow(100)
    val pitchValue = _pitchValue.asStateFlow()

    private val _tempoValue = MutableStateFlow(100)
    val tempoValue = _tempoValue.asStateFlow()

    fun updatePitchValue(value: Int){
        _pitchValue.value = value
    }

    fun initPitchValue(){
        _pitchValue.value = 100
        setPitch()
    }

    fun setPitch() {
        val action = MediaSessionCallback.SET_PITCH
        val bundle = Bundle().apply {
            putInt("value", pitchValue.value)
        }
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
    }

    fun updateTempoValue(value: Int){
        _tempoValue.value = value
    }

    fun initTempoValue(){
        _tempoValue.value = 100
        setTempo()
    }

    fun setTempo() {
        val action = MediaSessionCallback.SET_TEMPO
        val bundle = Bundle().apply {
            putInt("value", tempoValue.value)
        }
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
    }

    private val _isEqualizerEnabled = MutableStateFlow(false)
    val isEqualizerEnabled = _isEqualizerEnabled.asStateFlow()

    private val _equalizerCurrentPreset = MutableStateFlow(EqualizerPresets.PRESET_DEFAULT)
    val equalizerCurrentPreset = _equalizerCurrentPreset.asStateFlow()

    private val _equalizerSettings = MutableStateFlow(EqualizerSettings())
    val equalizerSettings: StateFlow<EqualizerSettings> = _equalizerSettings

    fun updateIsEqualizerEnabled(){
        if (isEqualizerEnabled.value)
            initEqualizerValue()
        _isEqualizerEnabled.value = !isEqualizerEnabled.value

    }

    fun initEqualizerValue(){
        updateEqualizerWithPreset(EqualizerPresets.PRESET_DEFAULT)
    }

    fun updateEqualizerWithPreset(presetIndex: Int) {
        _equalizerCurrentPreset.value = presetIndex
        val presetValues = EqualizerPresets.getPresetGainValues(presetIndex)
        _equalizerSettings.value = EqualizerSettings(
            bandLevels = presetValues.map{it.toFloat()},
            presetName = EqualizerPresets.effectTypes[presetIndex]
        )
        setEqualizerWithPreset()
    }

    fun setEqualizerWithPreset() {
        if (!isEqualizerEnabled.value) return

        val action = MediaSessionCallback.SET_EQUALIZER_PRESET
        val bundle = Bundle().apply {
            putInt("value", equalizerCurrentPreset.value)
        }
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
    }

    fun setEqualizerWithCustomValue(changedBand: Int) {
        if (!isEqualizerEnabled.value) return

        val action = MediaSessionCallback.SET_EQUALIZER_CUSTOM
        val bundle = Bundle().apply {
            putInt("band", changedBand)
            putInt("level", equalizerSettings.value.bandLevels[changedBand].toInt())
        }
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
    }

    fun updateEqualizerBandLevel(index: Int, newValue: Float) {
        _equalizerSettings.update { currentSettings ->
            currentSettings.withUpdatedBandLevel(index, newValue)
        }
        _equalizerCurrentPreset.value = EqualizerPresets.PRESET_DEFAULT
    }

    private val _isReverbEnabled = MutableStateFlow(false)
    val isReverbEnabled = _isReverbEnabled.asStateFlow()

    private val _reverbCurrentPreset = MutableStateFlow(ReverbPresets.PRESET_NONE)
    val reverbCurrentPreset = _reverbCurrentPreset.asStateFlow()

    private val _reverbValue = MutableStateFlow(0)
    val reverbValue = _reverbValue.asStateFlow()

    fun updateIsReverbEnabled(){
        if (isReverbEnabled.value){
            _reverbCurrentPreset.value = ReverbPresets.PRESET_NONE
            initReverbValue()
        }
        _isReverbEnabled.value = !isReverbEnabled.value
    }

    fun updateReverbCurrentPreset(presetIndex: Int){
        _reverbCurrentPreset.value = presetIndex
        setPresetReverb()
    }

    fun updateReverbValue(value: Int){
        _reverbValue.value = value
    }

    fun initReverbValue(){
        _reverbValue.value = 0
        setPresetReverb()
    }

    fun setPresetReverb() {
        if (!isReverbEnabled.value) return

        val action = MediaSessionCallback.SET_REVERB
        val bundle = Bundle().apply {
            putInt("presetIndex", reverbCurrentPreset.value)
            putInt("sendLevel",reverbValue.value)
        }
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
    }

    private val _bassBoostValue = MutableStateFlow(0)
    val bassBoostValue = _bassBoostValue.asStateFlow()

    fun updateBassBoostValue(value: Int){
        _bassBoostValue.value = value
        setBassBoost()
    }

    fun initBassBoostValue(){
        _bassBoostValue.value = 0
        setBassBoost()
    }

    fun setBassBoost() {
        val action = MediaSessionCallback.SET_BASS_BOOST
        val bundle = Bundle().apply {
            putInt("value", bassBoostValue.value)
        }
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
    }

    private val _loudnessEnhancerValue = MutableStateFlow(0)
    val loudnessEnhancerValue = _loudnessEnhancerValue.asStateFlow()

    fun updateLoudnessEnhancerValue(value: Int){
        _loudnessEnhancerValue.value = value
    }
    fun initLoudnessEnhancerValue(){
        _loudnessEnhancerValue.value = 0
        setLoudnessEnhancer()
    }

    fun setLoudnessEnhancer() {
        val action = MediaSessionCallback.SET_LOUDNESS_ENHANCER
        val bundle = Bundle().apply {
            putInt("value", loudnessEnhancerValue.value)
        }
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
    }


    private val _virtualizerValue = MutableStateFlow(0)
    val virtualizerValue = _virtualizerValue.asStateFlow()

    fun updateVirtualizerValue(value: Int){
        _virtualizerValue.value = value
        setVirtualizer()
    }

    fun initVirtualizerValue(){
        _virtualizerValue.value = 0
        setVirtualizer()
    }

    fun setVirtualizer() {
        val action = MediaSessionCallback.SET_VIRTUALIZER
        val bundle = Bundle().apply {
            putInt("value", virtualizerValue.value)
        }
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)    }


    fun setEnvironmentalReverb() {
        TODO("Not yet implemented")
    }

    fun releaseMediaController() {
        viewModelScope.launch {
            mediaController.value?.let { controller ->
                controller.release()
                _mediaController.value = null
            }
            // MediaService 종료
            application.stopService(Intent(application, MediaService::class.java))
        }
    }


}