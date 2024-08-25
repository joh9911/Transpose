package com.example.transpose.data.repository.local_file

import android.net.Uri
import com.example.transpose.data.model.local_file.LocalFileData

interface LocalFileRepository {
    fun getAudioFiles(): Result<List<LocalFileData>>
    fun getVideoFiles(): Result<List<LocalFileData>>
    fun deleteFile(file: LocalFileData): Result<Boolean>

}