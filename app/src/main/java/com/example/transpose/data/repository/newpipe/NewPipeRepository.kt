package com.example.transpose.data.repository.newpipe

import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.data.model.newpipe.NewPipePlaylistData
import com.example.transpose.data.repository.PlaylistPager
import com.example.transpose.data.repository.VideoPager
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.VideoStream

interface NewPipeRepository{
    suspend fun createPlaylistPager(playlistId: String): PlaylistPager
    suspend fun fetchPlaylistResult(pager: PlaylistPager): Result<NewPipePlaylistData?>
    suspend fun fetchPlaylistWithChannelId(channelId: String): Result<List<NewPipeContentListData>?>

    suspend fun fetchPlaylistItemsResult(playlistPager: PlaylistPager): Result<List<NewPipeContentListData>>
    suspend fun createSearchPager(query: String): VideoPager
    suspend fun fetchSearchResult(pager: VideoPager): Result<List<NewPipeContentListData>>
    suspend fun fetchSeparatedStreamByVideoId(videoId: String): Result<Pair<MutableList<VideoStream>?, MutableList<AudioStream>>>
    suspend fun fetchVideoStreamByVideoId(videoId: String): Result<MutableList<VideoStream>>
}