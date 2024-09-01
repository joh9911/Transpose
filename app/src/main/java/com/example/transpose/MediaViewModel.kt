package com.example.transpose

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.example.transpose.data.model.local_file.LocalFileData
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
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.VideoStream
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

    private val _isShowingThumbnail = MutableStateFlow(false)
    val isShowingThumbnail = _isShowingThumbnail.asStateFlow()

    private val _isShowingLoadingIndicator = MutableStateFlow(false)
    val isShowingLoadingIndicator = _isShowingLoadingIndicator.asStateFlow()

    init {
        initializeMediaController()
    }

    private fun initializeMediaController() {
        val sessionToken =
            SessionToken(application, ComponentName(application, MediaService::class.java))
        val controllerFuture = MediaController.Builder(application, sessionToken)
            .buildAsync()
        controllerFuture.addListener(
            {
                _mediaController.value = controllerFuture.get()
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
            Logger.d("onMediaMetadataChanged")
            super.onMediaMetadataChanged(mediaMetadata)
        }
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            Logger.d("onTracksChanged $tracks")
        }
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Logger.e("PlaybackError: ${error.message}")
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying

        }


        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> Log.d("CustomPlayerState", "Idle")
                Player.STATE_BUFFERING -> Log.d("CustomPlayerState", "Buffering")
                Player.STATE_READY -> Log.d("CustomPlayerState", "Ready")
                Player.STATE_ENDED -> Log.d("CustomPlayerState", "Ended")
            }
            updatePlaybackState()
        }
    }

    private fun updatePlaybackState() {
        val controller = _mediaController.value ?: return
        _isPlaying.value = controller.isPlaying
        _duration.value = controller.duration
        _currentPosition.value = controller.currentPosition
    }

    private val _currentVideoItem = MutableStateFlow<NewPipeVideoData?>(null)
    val currentVideoItem = _currentVideoItem.asStateFlow()

    private val _availableResolutions = MutableStateFlow<List<String>>(emptyList())
    val availableResolutions: StateFlow<List<String>> = _availableResolutions

    private var currentVideoStreams: List<VideoStream>? = null
    private var currentAudioStream: AudioStream? = null


    fun updateCurrentVideoItem(item: NewPipeVideoData){
        _currentVideoItem.value = item
        setMediaItemForNewPipeDataTemp(item)
    }

    fun removeCurrentMediaItem(){
        _currentVideoItem.value = null
        mediaController.value?.removeMediaItem(0)
    }

    fun setMediaItemForLocalFile(item: LocalFileData){
        mediaController.value?.let { controller ->
            val mediaItem = MediaItem.Builder()
                .setUri(item.uri)
                .setMediaId(item.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(item.title)
                        .setArtist(item.artist ?: "Unknown Uploader")
                        .build()
                )
                .build()
            controller.setMediaItem(mediaItem)
        }
    }

    private fun setMediaItemForNewPipeDataTemp(item: NewPipeVideoData){
        viewModelScope.launch {
            try {
                val streamInfo = getVideoStreamByVideoId(item.id)
                streamInfo.let { videoStream ->
                    val selectedVideoStream = videoStream?.maxByOrNull { it.getResolution() }
                    if (selectedVideoStream != null) {
                        val mediaItem = MediaItem.Builder()
                            .setMediaId(item.id)
                            .setUri(selectedVideoStream.content)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(item.title)
                                    .setArtist(item.uploaderName ?: "Unknown Uploader")
                                    .setArtworkUri(item.thumbnailUrl?.let { Uri.parse(it) })
                                    .setDescription(item.description)
                                    .build()
                            )
                            .build()

                        mediaController.value?.setMediaItem(mediaItem)
                        mediaController.value?.prepare()
                        mediaController.value?.play()
                    }
                }
            } catch (e: Exception) {
                Logger.d("setMediaItemForNewPipeDataTemp $e")
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun setMediaItemForNewPipeData(item: NewPipeVideoData) {
        viewModelScope.launch {
            try {
                val streamInfo = getSeparatedStreamByVideoId(item.id)
                streamInfo?.let { (videoStreams, audioStreams) ->
                    val selectedVideoStream = videoStreams?.minByOrNull { it.getResolution() }
                    val selectedAudioStream = audioStreams.firstOrNull()
                    if (selectedVideoStream != null && selectedAudioStream != null) {
                        Logger.d("${selectedVideoStream.content}               ${selectedAudioStream.content}")
                        val mediaItem = MediaItem.Builder()
                            .setMediaId(item.id)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setExtras(Bundle().apply {
                                        putString("videoUrl",selectedVideoStream.content)
                                        putString("audioUrl",selectedAudioStream.content)
                                    })
                                    .setTitle(item.title)
                                    .setArtist(item.uploaderName ?: "Unknown Uploader")
                                    .setArtworkUri(item.thumbnailUrl?.let { Uri.parse(it) })
                                    .setDescription(item.description)
                                    .build()
                            )
                            .build()

                        mediaController.value?.setMediaItem(mediaItem)
                        mediaController.value?.prepare()
                        mediaController.value?.play()
                    }
                }
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    fun changeResolution(resolution: String){
        val selectedVideoStream = currentVideoStreams?.find { it.resolution == resolution }
        val currentItem = mediaController.value?.currentMediaItem


        if (selectedVideoStream != null && currentItem != null) {
            val currentPosition = mediaController.value?.currentPosition ?: 0

            val newMediaItem = MediaItem.Builder()
                .setMediaId(currentItem.mediaId)
                .setUri(selectedVideoStream.content)
                .setMediaMetadata(currentItem.mediaMetadata)
                .setTag(Bundle().apply {
                    putString("audioUrl", currentAudioStream?.content)
                })
                .build()

            mediaController.value?.setMediaItem(newMediaItem, currentPosition)
            mediaController.value?.prepare()
            mediaController.value?.play()
        }

    }

    fun playPause(){
        if (isPlaying.value)
            mediaController.value?.pause()
        else
            mediaController.value?.play()
    }

    private suspend fun getVideoStreamByVideoId(videoId: String): MutableList<VideoStream>?{
        _isShowingThumbnail.value = true
        _isShowingLoadingIndicator.value = true
        return withContext(Dispatchers.IO){
            try {
                val result = newPipeRepository.fetchVideoStreamByVideoId(videoId)
                if (result.isSuccess) {
                    _isShowingThumbnail.value = false
                    _isShowingLoadingIndicator.value = false
                    result.getOrNull()
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

    private suspend fun getSeparatedStreamByVideoId(videoId: String): Pair<MutableList<VideoStream>?, MutableList<AudioStream>>? {
        _isShowingThumbnail.value = true
        _isShowingLoadingIndicator.value = true
        return withContext(Dispatchers.IO) {
            try {
                val result = newPipeRepository.fetchSeparatedStreamByVideoId(videoId)
                if (result.isSuccess) {
                    _isShowingThumbnail.value = false
                    _isShowingLoadingIndicator.value = false
                    result.getOrNull()
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


    fun getAvailableResolutions(streamInfo: StreamInfo): List<String> {
        val videoStreams = streamInfo.videoStreams + streamInfo.videoOnlyStreams
        return videoStreams
            .map { it.getResolution() }
            .distinct()
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

    fun pitchPlusOne(){
        val action = MediaSessionCallback.PITCH_PLUS
        val bundle = Bundle()
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
    }

    fun pitchMinusOne(){
        val action = MediaSessionCallback.PITCH_MINUS
        val bundle = Bundle()
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
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

    fun tempoPlusOne(){
        val action = MediaSessionCallback.TEMPO_PLUS
        val bundle = Bundle()
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
    }

    fun tempoMinusOne(){
        val action = MediaSessionCallback.TEMPO_MINUS
        val bundle = Bundle()
        val sessionCommand = SessionCommand(action, bundle)
        mediaController.value?.sendCustomCommand(sessionCommand, bundle)
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

    private fun setEqualizerWithPreset() {
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