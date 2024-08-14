package com.example.transpose.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.model.NewPipeContentListData
import com.example.transpose.data.model.NewPipePlaylistData
import com.example.transpose.data.repository.MusicCategoryRepository
import com.example.transpose.data.repository.NewPipeException
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

    private val _nationalPlaylistState = MutableStateFlow(PlaylistState())
    val nationalPlaylistState: StateFlow<PlaylistState> = _nationalPlaylistState.asStateFlow()

    private val _recommendedPlaylistState = MutableStateFlow(PlaylistState())
    val recommendedPlaylistState: StateFlow<PlaylistState> = _recommendedPlaylistState.asStateFlow()

    private val _typedPlaylistState = MutableStateFlow(PlaylistState())
    val typedPlaylistState: StateFlow<PlaylistState> = _typedPlaylistState.asStateFlow()


    fun fetchNationalPlaylists() = viewModelScope.launch(Dispatchers.IO) {
        _nationalPlaylistState.value = _nationalPlaylistState.value.copy(uiState = UiState.Loading)
        val currentList = mutableListOf<NewPipePlaylistData>()
        var hasError = false

        val nationPlaylistUrls = MusicCategoryRepository().nationalPlaylistUrls
        nationPlaylistUrls.forEach { playlistId ->

            val result = async { newPipeRepository.fetchPlaylistData(playlistId) }.await()

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

        // 모든 요청이 완료된 후 최종 상태 설정
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

        val result = async { newPipeRepository.fetchPlaylistWithChannelId(recommendedId) }.await()

        when {
            result.isSuccess -> {
                val contents = result.getOrNull()
                Logger.d("가져온 결과, $contents")
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

        val result = async { newPipeRepository.fetchPlaylistWithChannelId(typedPlaylistId) }.await()

        when {
            result.isSuccess -> {
                val contents = result.getOrNull()
                Logger.d("가져온 결과, $contents")
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

    private val _searchUiState = MutableStateFlow<UiState>(UiState.Initial)
    val searchUiState= _searchUiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<NewPipeContentListData>>(emptyList())
    val searchResults= _searchResults.asStateFlow()

    fun fetchSearchResult(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _searchUiState.value = UiState.Loading

        try {
            val searchResults = newPipeRepository.fetchSearchResult(query)

            if (searchResults.isSuccess){
                _searchResults.value = searchResults.getOrElse { emptyList() }
                _searchUiState.value = UiState.Success

                Logger.d("First page results: ${searchResults.getOrNull()} items")

            }
            if (searchResults.isFailure){

                Logger.d("failure: ${searchResults.exceptionOrNull()} items")

                _searchResults.value = emptyList()
                _searchUiState.value = UiState.Error(searchResults.exceptionOrNull().toString())
            }


//            firstPageResults.forEachIndexed { index, item ->
//                when (item) {
//                    is NewPipeVideoData -> {
//                        Logger.d(
//                            "Video Item $index: " +
//                                    "Title: ${item.title}, " +
//                                    "Channel: ${item.uploaderName}, " +
//                                    "Video ID: ${item.id}, " +
//                                    "Date: ${item.publishTimestamp}, " +
//                                    "Duration: ${item.duration}, " +
//                                    "View Count: ${item.viewCount}"
//                        )
//                    }
//                    is NewPipePlaylistData -> {
//                        Logger.d(
//                            "Playlist Item $index: " +
//                                    "Title: ${item.title}, " +
//                                    "Uploader: ${item.uploaderName}, " +
//                                    "Playlist ID: ${item.id}, " +
//                                    "Stream Count: ${item.streamCount}"
//                        )
//                    }
//                    is NewPipeChannelData -> {
//                        Logger.d(
//                            "Channel Item $index: " +
//                                    "Title: ${item.title}, " +
//                                    "Channel ID: ${item.id}, " +
//                                    "Subscriber Count: ${item.subscriberCount}, " +
//                                    "Stream Count: ${item.streamCount}"
//                        )
//                    }
//                    else -> {
//                        Logger.d("Unknown item type at index $index")
//                    }
//                }
//            }

            // 다음 페이지 로직은 주석 처리된 상태로 유지
        } catch (e: Exception) {
            Logger.e("Error fetching search results", e)
            _searchUiState.value = UiState.Error(e.message ?: "Unknown error occurred")
        }
    }
}