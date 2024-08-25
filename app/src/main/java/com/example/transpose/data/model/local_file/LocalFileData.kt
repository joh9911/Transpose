package com.example.transpose.data.model.local_file

import android.net.Uri

data class LocalFileData(
    val id: Long,
    val title: String,
    val uri: Uri,
    val mimeType: String,
    val size: Long,
    val duration: Long
)