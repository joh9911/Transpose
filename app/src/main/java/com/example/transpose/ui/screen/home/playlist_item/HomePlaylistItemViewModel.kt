package com.example.transpose.ui.screen.home.playlist_item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.model.NewPipeContentListData
import com.example.transpose.data.model.NewPipePlaylistData
import com.example.transpose.data.repository.PlaylistPager
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.ui.common.UiState
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
) : ViewModel(){

    // PlaylistItemScreen
    private val _playlistItemUiState = MutableStateFlow<UiState>(UiState.Initial)
    val playlistItemUiState = _playlistItemUiState.asStateFlow()

    private val _playlistItems = MutableStateFlow<List<NewPipeContentListData>>(emptyList())
    val playlistItems = _playlistItems.asStateFlow()

    private val _playlistInfo = MutableStateFlow<NewPipePlaylistData?>(null)
    val playlistInfo = _playlistInfo.asStateFlow()

    private val _hasMorePlaylistItems = MutableStateFlow(true)
    val hasMorePlaylistItems = _hasMorePlaylistItems.asStateFlow()


    private val _isMorePlaylistItemsLoading = MutableStateFlow(false)
    val isMorePlaylistItemsLoading = _isMorePlaylistItemsLoading.asStateFlow()

    private var playlistPager: PlaylistPager? = null

    fun initializePlaylistPager(playlistId: String) = viewModelScope.launch(Dispatchers.IO){
        _playlistItemUiState.value = UiState.Loading
        try {
            playlistPager = newPipeRepository.createPlaylistPager(playlistId)
            firstFetchPlaylistItems()
        }catch (e: Exception){
            _playlistItemUiState.value = UiState.Error(e.toString())
        }
    }

    private fun firstFetchPlaylistItems() = viewModelScope.launch(Dispatchers.IO){
        playlistPager ?: return@launch
        try {
            _playlistInfo.value = playlistPager!!.getPlaylist()
            val playlistItemsResult = newPipeRepository.fetchPlaylistItemsResult(playlistPager!!)

            if (playlistItemsResult.isSuccess){
                _playlistItems.value = playlistItemsResult.getOrElse { emptyList() }
                _playlistItemUiState.value = UiState.Success
                _hasMorePlaylistItems.value = playlistPager!!.isHasNextPage()
            }
            if (playlistItemsResult.isFailure){
                _playlistItems.value = emptyList()
                _playlistItemUiState.value = UiState.Error(playlistItemsResult.exceptionOrNull().toString())
            }
            // 다음 페이지 로직은 주석 처리된 상태로 유지
        } catch (e: Exception) {
            Logger.e("Error fetching search results", e)
            _playlistItemUiState.value = UiState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun loadMorePlaylistItems() = viewModelScope.launch(Dispatchers.IO) {
        _isMorePlaylistItemsLoading.value = true
        playlistPager ?: return@launch

        try {
            val result = newPipeRepository.fetchPlaylistItemsResult(playlistPager!!)
            if (result.isSuccess){
                _playlistItems.value += result.getOrDefault(emptyList())
                _isMorePlaylistItemsLoading.value = false
                _hasMorePlaylistItems.value = playlistPager!!.isHasNextPage()
            }
            if (result.isFailure){
                Logger.d("loadMoreSearchResults ${result.exceptionOrNull()}")
                _isMorePlaylistItemsLoading.value = false
            }
        }catch (e: Exception){
            _isMorePlaylistItemsLoading.value = false
            Logger.d("loadMoreSearchResults catch ${e}")
        }
    }
}