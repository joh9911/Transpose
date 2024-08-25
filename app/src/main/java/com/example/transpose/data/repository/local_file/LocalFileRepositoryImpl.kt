package com.example.transpose.data.repository.local_file

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.transpose.data.model.local_file.LocalFileData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalFileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): LocalFileRepository{
    override fun getAudioFiles(): Result<List<LocalFileData>> {
        return queryMediaStore(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
    }

    override fun getVideoFiles(): Result<List<LocalFileData>> {
        return queryMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
    }

    private fun queryMediaStore(uri: Uri): Result<List<LocalFileData>> {
        return runCatching {
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DURATION
            )

            val mediaFiles = mutableListOf<LocalFileData>()

            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(uri, id)

                    val mediaFile = LocalFileData(
                        id = id,
                        title = cursor.getString(titleColumn),
                        uri = contentUri,
                        mimeType = cursor.getString(mimeTypeColumn),
                        size = cursor.getLong(sizeColumn),
                        duration = cursor.getLong(durationColumn)
                    )
                    mediaFiles.add(mediaFile)
                }
            }

            mediaFiles
        }
    }

    override fun deleteFile(file: LocalFileData): Result<Boolean> {
        return runCatching {
            val deletedRows = context.contentResolver.delete(file.uri, null, null)
            deletedRows > 0
        }
    }

}