package com.example.transpose.data.model

data class PlaylistInfoModel(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channelName: String,
    val channelAvatarUrl: String,
    val lastUpdated: String,
    val videoCount: Int
)