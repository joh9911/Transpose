package com.example.transpose.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.model.NewPipeContentListData
import com.example.transpose.data.model.NewPipePlaylistData
import com.example.transpose.data.repository.MusicCategoryRepository
import com.example.transpose.data.repository.NewPipeException
import com.example.transpose.data.repository.PlaylistPager
import com.example.transpose.data.repository.VideoPager
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.ui.common.UiState
import com.example.transpose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newPipeRepository: NewPipeRepository
) : ViewModel() {


    // Playlist Screen
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



    // SearchResult Screen
    private val _searchUiState = MutableStateFlow<UiState>(UiState.Initial)
    val searchUiState= _searchUiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<NewPipeContentListData>>(emptyList())
    val searchResults= _searchResults.asStateFlow()

    private val _hasMoreSearchItems = MutableStateFlow(false)
    val hasMoreSearchItems = _hasMoreSearchItems.asStateFlow()

    private val _isMoreSearchItemsLoading = MutableStateFlow(false)
    val isMoreItemsLoading = _isMoreSearchItemsLoading.asStateFlow()

    private var searchPager: VideoPager? = null

    fun initializeSearchPager(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _searchUiState.value = UiState.Loading
        try {
            searchPager = newPipeRepository.createSearchPager(query)
            firstFetchSearchResult()
        }catch (e: Exception){
            _searchUiState.value = UiState.Error(e.toString())
        }
    }

    private fun firstFetchSearchResult() = viewModelScope.launch(Dispatchers.IO) {
        searchPager ?: return@launch
        try {
            val searchResults = newPipeRepository.fetchSearchResult(searchPager!!)

            if (searchResults.isSuccess){
                _searchResults.value = searchResults.getOrElse { emptyList() }
                _searchUiState.value = UiState.Success
                _hasMoreSearchItems.value = searchPager!!.isHasNextPage()
            }
            if (searchResults.isFailure){
                _searchResults.value = emptyList()
                _searchUiState.value = UiState.Error(searchResults.exceptionOrNull().toString())
            }
            // 다음 페이지 로직은 주석 처리된 상태로 유지
        } catch (e: Exception) {
            Logger.e("Error fetching search results", e)
            _searchUiState.value = UiState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun loadMoreSearchResults() = viewModelScope.launch(Dispatchers.IO) {
        _isMoreSearchItemsLoading.value = false
        searchPager ?: return@launch

        try {
            val result = newPipeRepository.fetchSearchResult(searchPager!!)
            if (result.isSuccess){
                _searchResults.value += result.getOrDefault(emptyList())
                _isMoreSearchItemsLoading.value = false
                _hasMoreSearchItems.value = searchPager!!.isHasNextPage()
            }
            if (result.isFailure){
                Logger.d("loadMoreSearchResults ${result.exceptionOrNull()}")
                _isMoreSearchItemsLoading.value = false
            }
        }catch (e: Exception){
            _isMoreSearchItemsLoading.value = false
            Logger.d("loadMoreSearchResults catch ${e}")
        }
    }

    fun getStreamInfoByVideoId(videoId: String) = viewModelScope.launch(Dispatchers.IO){
        try {
            val result = newPipeRepository.fetchStreamInfoByVideoId(videoId)
            if (result.isSuccess){
                val bestQualityStream = result.getOrNull()?.maxByOrNull { it.getResolution() }
                bestQualityStream?.let {
                    Logger.d("getStreamInfoByVideoId ${it.content}")
                }
            }
            if (result.isFailure){
                Logger.d("getStreamInfoByVideoId ${result.exceptionOrNull()}")
            }
        }catch (e: Exception){
            Logger.d("getStreamInfoByVideoId ${e}")
        }
    }



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
            _searchUiState.value = UiState.Error(e.toString())
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