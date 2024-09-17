package com.example.transpose.ui.screen.home.playlist_item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.model.newpipe.NewPipePlaylistData
import com.example.transpose.data.repository.PlaylistPager
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.ui.common.PaginatedState
import com.example.transpose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePlaylistItemViewModel @Inject constructor(
    private val newPipeRepository: NewPipeRepository
) : ViewModel() {

    private val _playlistItemsState = MutableStateFlow<PaginatedState>(PaginatedState.Initial)
    val playlistItemsState = _playlistItemsState.asStateFlow()

    private val _playlistInfo = MutableStateFlow<NewPipePlaylistData?>(null)
    val playlistInfo = _playlistInfo.asStateFlow()

    private var playlistPager: PlaylistPager? = null

    fun initializePlaylistPager(playlistId: String) = viewModelScope.launch(Dispatchers.IO) {
        _playlistItemsState.value = PaginatedState.Loading
        try {
            playlistPager = newPipeRepository.createPlaylistPager(playlistId)
            _playlistInfo.value = playlistPager!!.getPlaylist()
            firstFetchPlaylistItems()
        } catch (e: Exception) {
            _playlistItemsState.value = PaginatedState.Error(e.toString())
        }
    }

    private fun firstFetchPlaylistItems() = viewModelScope.launch(Dispatchers.IO) {
        playlistPager ?: return@launch
        try {
            val playlistItemsResult = newPipeRepository.fetchPlaylistItemsResult(playlistPager!!)

            if (playlistItemsResult.isSuccess) {
                val items = playlistItemsResult.getOrElse { emptyList() }

                _playlistItemsState.value = PaginatedState.Success(
                    items = items,
                    hasMore = playlistPager!!.isHasNextPage(),
                    isLoadingMore = false
                )
            } else {
                _playlistItemsState.value = PaginatedState.Error(playlistItemsResult.exceptionOrNull().toString())
            }
        } catch (e: Exception) {
            Logger.e("Error fetching playlist items", e)
            _playlistItemsState.value = PaginatedState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun loadMorePlaylistItems() = viewModelScope.launch(Dispatchers.IO) {
        val currentState = _playlistItemsState.value
        if (currentState !is PaginatedState.Success || currentState.isLoadingMore) return@launch

        _playlistItemsState.value = currentState.copy(isLoadingMore = true)

        try {
            val result = newPipeRepository.fetchPlaylistItemsResult(playlistPager!!)
            if (result.isSuccess) {
                val newItems = result.getOrDefault(emptyList())
                _playlistItemsState.value = currentState.copy(
                    items = currentState.items + newItems,
                    hasMore = playlistPager!!.isHasNextPage(),
                    isLoadingMore = false
                )
            } else {
                _playlistItemsState.value = currentState.copy(
                    isLoadingMore = false,
                    loadMoreError = result.exceptionOrNull()?.toString()
                )
                Logger.d("loadMorePlaylistItems ${result.exceptionOrNull()}")
            }
        } catch (e: Exception) {
            _playlistItemsState.value = currentState.copy(
                isLoadingMore = false,
                loadMoreError = e.toString()
            )
            Logger.d("loadMorePlaylistItems catch ${e}")
        }
    }
}