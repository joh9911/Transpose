package com.example.transpose.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.transpose.data.database.entity.VideoEntity

@Dao
interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Query("SELECT * FROM videos WHERE playlistId = :playlistId")
    suspend fun getVideosForPlaylist(playlistId: Long): List<VideoEntity>

    @Delete
    suspend fun deleteVideo(video: VideoEntity)
}