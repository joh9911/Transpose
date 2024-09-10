package com.example.transpose.ui.screen.library.my_playlist_item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.database.entity.VideoEntity
import com.example.transpose.data.repository.database.MyPlaylistDBRepositoryImpl
import com.example.transpose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryMyPlaylistItemViewModel @Inject constructor(
    private val myPlaylistDBRepositoryImpl: MyPlaylistDBRepositoryImpl
): ViewModel() {


    private val _myPlaylistItems = MutableStateFlow<List<VideoEntity>>(emptyList())
    val myPlaylistItems = _myPlaylistItems.asStateFlow()

    fun getVideosForPlaylist(playlistId: Long) = viewModelScope.launch {
        try {
            _myPlaylistItems.value = myPlaylistDBRepositoryImpl.getVideosForPlaylist(playlistId)

        }catch (e: Exception){
            Logger.d("getVideosForPlaylist $e")
        }
    }

    fun deleteVideo(playlistId: Long, videoEntity: VideoEntity) = viewModelScope.launch {
        try {
            myPlaylistDBRepositoryImpl.deleteVideo(videoEntity)
            getVideosForPlaylist(playlistId)
        }catch (e: Exception){
            Logger.d("deleteVideo")
        }
    }
}