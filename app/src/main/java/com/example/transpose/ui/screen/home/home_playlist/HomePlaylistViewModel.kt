package com.example.transpose.ui.screen.home.home_playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.model.NewPipePlaylistData
import com.example.transpose.data.repository.MusicCategoryRepository
import com.example.transpose.data.repository.PlaylistPager
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePlaylistViewModel @Inject constructor(
    private val newPipeRepository: NewPipeRepository
) : ViewModel(){


    private val _nationalPlaylistState = MutableStateFlow(PlaylistState())
    val nationalPlaylistState: StateFlow<PlaylistState> = _nationalPlaylistState.asStateFlow()

    private var nationalPlaylistPager: List<PlaylistPager> = emptyList()

    private val _recommendedPlaylistState = MutableStateFlow(PlaylistState())
    val recommendedPlaylistState: StateFlow<PlaylistState> = _recommendedPlaylistState.asStateFlow()

    private val _typedPlaylistState = MutableStateFlow(PlaylistState())
    val typedPlaylistState: StateFlow<PlaylistState> = _typedPlaylistState.asStateFlow()


    fun fetchNationalPlaylists() = viewModelScope.launch(Dispatchers.IO) {

        _nationalPlaylistState.value = _nationalPlaylistState.value.copy(uiState = UiState.Loading)
        val playlistPagerList = mutableListOf<PlaylistPager>()
        val currentList = mutableListOf<NewPipePlaylistData>()
        var hasError = false

        val nationPlaylistUrls = MusicCategoryRepository().nationalPlaylistUrls
        nationPlaylistUrls.forEach { playlistId ->
            val playlistPager = newPipeRepository.createPlaylistPager(playlistId)
            playlistPagerList.add(playlistPager)
            nationalPlaylistPager = playlistPagerList
            val result = newPipeRepository.fetchPlaylistResult(playlistPager)

            when {
                result.isSuccess -> {
                    val playlistData = result.getOrNull()

                    playlistData?.let {
                        currentList.add(playlistData)
                        _nationalPlaylistState.value =
                            PlaylistState(items = currentList.toList(), uiState = UiState.Success)
                    }
                }

                result.isFailure -> {
                    hasError = true
                }
            }
            // firstPageItems를 처리할 수 있습니다 (필요한 경우).
        }

        if (hasError && currentList.isEmpty()) {
            _nationalPlaylistState.value =
                PlaylistState(uiState = UiState.Error("Failed to fetch playlists"))
        } else if (hasError) {
            _nationalPlaylistState.value = PlaylistState(
                items = currentList,
                uiState = UiState.Error("Some playlists failed to load")
            )
        } else {
            _nationalPlaylistState.value =
                PlaylistState(items = currentList, uiState = UiState.Success)
        }
    }

    fun fetchRecommendedPlaylists() = viewModelScope.launch(Dispatchers.IO) {

        _recommendedPlaylistState.value = _recommendedPlaylistState.value.copy(uiState = UiState.Loading)

        val recommendedId = MusicCategoryRepository().recommendPlaylistChannelId

        val result = newPipeRepository.fetchPlaylistWithChannelId(recommendedId)

        when {
            result.isSuccess -> {
                val contents = result.getOrNull()
                contents?.let { contentList ->
                    val playlists = contentList.filterIsInstance<NewPipePlaylistData>()
                    if (playlists.isNotEmpty()) {
                        _recommendedPlaylistState.value = PlaylistState(items = playlists, uiState = UiState.Success)
                    } else {
                        _recommendedPlaylistState.value = PlaylistState(items = emptyList(), uiState = UiState.Error("No playlists found"))
                    }
                } ?: run {
                    _recommendedPlaylistState.value = PlaylistState(items = emptyList(), uiState = UiState.Error("No content found"))
                }
            }
            result.isFailure -> {
                _recommendedPlaylistState.value = PlaylistState(items = emptyList(), uiState = UiState.Error("${result.exceptionOrNull()}"))
            }
        }
    }


    fun fetchTypedPlaylists() = viewModelScope.launch(Dispatchers.IO) {

        _typedPlaylistState.value = _typedPlaylistState.value.copy(uiState = UiState.Loading)

        val typedPlaylistId = MusicCategoryRepository().typedPlaylistChannelId

        val result = newPipeRepository.fetchPlaylistWithChannelId(typedPlaylistId)

        when {
            result.isSuccess -> {
                val contents = result.getOrNull()
                contents?.let { contentList ->
                    val playlists = contentList.filterIsInstance<NewPipePlaylistData>()
                    if (playlists.isNotEmpty()) {
                        _typedPlaylistState.value = PlaylistState(items = playlists, uiState = UiState.Success)
                    } else {
                        _typedPlaylistState.value = PlaylistState(items = emptyList(), uiState = UiState.Error("No playlists found"))
                    }
                } ?: run {
                    _typedPlaylistState.value = PlaylistState(items = emptyList(), uiState = UiState.Error("No content found"))
                }
            }
            result.isFailure -> {
                _typedPlaylistState.value = PlaylistState(items = emptyList(), uiState = UiState.Error("${result.exceptionOrNull()}"))
            }
        }
    }

    data class PlaylistState(
        val items: List<NewPipePlaylistData> = emptyList(),
        val uiState: UiState = UiState.Initial
    )


}