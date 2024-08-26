package com.example.transpose.data.model.local_file

import android.net.Uri

data class LocalFileData(
    val id: Long,
    val title: String,
    val uri: Uri,
    val mimeType: String,
    val size: Long,
    val duration: Long,
    val artist: String?,
    val album: String?,
    val year: Int?,
    val genre: String?,
    val composer: String?,
    val albumArtist: String?,
    val width: Int?,  // 비디오용
    val height: Int?, // 비디오용
    val resolution: String?, // 비디오용
    val dateTaken: Long?,
    val dateAdded: Long,
    val dateModified: Long
)