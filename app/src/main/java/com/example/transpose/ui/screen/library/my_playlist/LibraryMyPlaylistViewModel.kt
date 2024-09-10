package com.example.transpose.ui.screen.library.my_playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.database.entity.PlaylistEntity
import com.example.transpose.data.model.local_file.LocalFileData
import com.example.transpose.data.repository.database.MyPlaylistDBRepository
import com.example.transpose.data.repository.database.MyPlaylistDBRepositoryImpl
import com.example.transpose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryMyPlaylistViewModel @Inject constructor(
    private val myPlaylistDBRepositoryImpl: MyPlaylistDBRepositoryImpl
): ViewModel() {

    private val _myPlaylists = MutableStateFlow<List<PlaylistEntity>>(emptyList())
    val myPlaylists = _myPlaylists.asStateFlow()

    init {
        getAllMyPlaylist()
    }

    fun createMyPlaylist(name: String) = viewModelScope.launch {
        try {
            myPlaylistDBRepositoryImpl.createPlaylist(name)

        }catch (e: Exception){
            Logger.d("createMyPlaylist $e")

        }finally {
            getAllMyPlaylist()
        }
    }

    private fun getAllMyPlaylist() = viewModelScope.launch {
        try {
            _myPlaylists.value = myPlaylistDBRepositoryImpl.getAllPlaylists()

        }catch (e: Exception){
            Logger.d("getAllMyPlaylist $e")
        }
    }

    fun deleteMyPlaylist(playlistEntity: PlaylistEntity) = viewModelScope.launch {
        try {
            myPlaylistDBRepositoryImpl.deletePlaylist(playlistEntity)
            getAllMyPlaylist()
        }catch (e: Exception){
            Logger.d("deleteMyPlaylist $e")
        }
    }

}