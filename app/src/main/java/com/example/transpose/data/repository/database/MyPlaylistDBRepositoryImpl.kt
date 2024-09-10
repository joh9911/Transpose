package com.example.transpose.data.repository.database

import com.example.transpose.data.database.dao.PlaylistDao
import com.example.transpose.data.database.dao.VideoDao
import com.example.transpose.data.database.entity.PlaylistEntity
import com.example.transpose.data.database.entity.VideoEntity
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.google.gson.Gson
import javax.inject.Inject

class MyPlaylistDBRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val videoDao: VideoDao
): MyPlaylistDBRepository {
    override suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    override suspend fun getAllPlaylists(): List<PlaylistEntity> {
        return playlistDao.getAllPlaylists()
    }

    override suspend fun deletePlaylist(playlist: PlaylistEntity) {
        return playlistDao.deletePlaylist(playlist)
    }

    override suspend fun addVideoToPlaylist(video: NewPipeVideoData, playlistId: Long) {
        videoDao.insertVideo(video.toVideoEntity(playlistId))
    }

    override suspend fun getVideosForPlaylist(playlistId: Long): List<VideoEntity> {
        return videoDao.getVideosForPlaylist(playlistId)
    }

    override suspend fun deleteVideo(videoEntity: VideoEntity) {
        return videoDao.deleteVideo(videoEntity)
    }
}

fun NewPipeVideoData.toVideoEntity(playlistId: Long): VideoEntity {
    return VideoEntity(
        id = id,
        playlistId = playlistId,
        title = title,
        description = description,
        publishTimestamp = publishTimestamp,
        thumbnailUrl = thumbnailUrl,
        uploaderName = uploaderName,
        uploaderUrl = uploaderUrl,
        uploaderAvatars = Gson().toJson(uploaderAvatars),
        uploaderVerified = uploaderVerified,
        duration = duration,
        viewCount = viewCount,
        textualUploadDate = textualUploadDate,
        streamType = streamType.name,
        shortFormContent = shortFormContent
    )
}