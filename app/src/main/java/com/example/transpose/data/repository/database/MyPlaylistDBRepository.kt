package com.example.transpose.data.repository.database

import com.example.transpose.data.database.entity.PlaylistEntity
import com.example.transpose.data.database.entity.VideoEntity
import com.example.transpose.data.model.newpipe.NewPipeVideoData

interface MyPlaylistDBRepository {
    suspend fun createPlaylist(name: String): Long
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    suspend fun addVideoToPlaylist(video: NewPipeVideoData, playlistId: Long)
    suspend fun getVideosForPlaylist(playlistId: Long): List<VideoEntity>
}