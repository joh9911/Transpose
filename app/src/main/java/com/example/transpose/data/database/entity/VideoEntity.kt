package com.example.transpose.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "videos",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("playlistId")]
)
data class VideoEntity(
    @PrimaryKey val id: String,
    val playlistId: Long,
    val title: String,
    val description: String,
    val publishTimestamp: Long?,
    val thumbnailUrl: String?,
    val uploaderName: String?,
    val uploaderUrl: String?,
    val uploaderAvatars: String, // JSON string
    val uploaderVerified: Boolean,
    val duration: Long,
    val viewCount: Long,
    val textualUploadDate: String?,
    val streamType: String,
    val shortFormContent: Boolean
)