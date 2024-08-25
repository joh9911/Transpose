package com.example.transpose.data.model.newpipe

import org.schabi.newpipe.extractor.playlist.PlaylistInfo

data class NewPipePlaylistData(
    override val id: String,
    override val title: String,
    override val description: String,
    override val publishTimestamp: Long?,
    override val thumbnailUrl: String?,
    val uploaderName: String,
    val uploaderUrl: String?,
    val uploaderVerified: Boolean,
    val streamCount: Long,
    val playlistType: PlaylistInfo.PlaylistType?
): NewPipeContentListData