package com.example.transpose.ui.screen.library.my_playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.repository.database.MyPlaylistDBRepository
import com.example.transpose.data.repository.database.MyPlaylistDBRepositoryImpl
import com.example.transpose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryMyPlaylistViewModel @Inject constructor(
    private val myPlaylistDBRepositoryImpl: MyPlaylistDBRepositoryImpl
): ViewModel() {

    fun createMyPlaylist(name: String) = viewModelScope.launch {
        try {
            myPlaylistDBRepositoryImpl.createPlaylist(name)

        }catch (e: Exception){
            Logger.d("createMyPlaylist $e")

        }
    }

    fun getAllMyPlaylist() = viewModelScope.launch {
        try {
            myPlaylistDBRepositoryImpl.getAllPlaylists()

        }catch (e: Exception){
            Logger.d("getAllMyPlaylist $e")
        }
    }

}