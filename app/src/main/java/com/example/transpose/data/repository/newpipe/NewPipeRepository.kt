package com.example.transpose.data.repository.newpipe

import com.example.transpose.data.model.NewPipeContentListData
import com.example.transpose.data.model.NewPipePlaylistData
import com.example.transpose.data.repository.PlaylistPager

interface NewPipeRepository{
    suspend fun fetchPlaylistData(playlistId: String): Result<NewPipePlaylistData?>
    suspend fun fetchPlaylistItemData(playlistPager: PlaylistPager): Result<List<NewPipeContentListData>>
    suspend fun fetchSearchResult(query: String): Result<List<NewPipeContentListData>>
    suspend fun fetchPlaylistWithChannelId(channelId: String): Result<List<NewPipeContentListData>?>
}