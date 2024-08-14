package com.example.transpose.data.repository

data class NewPipeVideoInfo(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String?,
    val uploaderName: String,
    val uploadDate: String,
    val viewCount: Long,
    val likeCount: Long,
    val duration: Long
)