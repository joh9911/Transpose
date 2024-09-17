package com.example.transpose.data.repository.local_file

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
        return queryMediaStore(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    override fun getVideoFiles(): Result<List<LocalFileData>> {
        return queryMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false)
    }


    private fun queryMediaStore(uri: Uri, isAudio: Boolean): Result<List<LocalFileData>> {
        return runCatching {
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.ARTIST,
                MediaStore.MediaColumns.ALBUM,
                MediaStore.MediaColumns.YEAR,
                MediaStore.MediaColumns.GENRE,
                MediaStore.MediaColumns.COMPOSER,
                MediaStore.MediaColumns.ALBUM_ARTIST,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.RESOLUTION,
                MediaStore.MediaColumns.DATE_TAKEN,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.DATE_MODIFIED
            )
            val selection = if (isAudio) "${MediaStore.Audio.Media.IS_MUSIC} != 0" else null

            val mediaFiles = mutableListOf<LocalFileData>()

            context.contentResolver.query(
                uri,
                projection,
                selection,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ALBUM)
                val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.YEAR)
                val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.GENRE)
                val composerColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.COMPOSER)
                val albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ALBUM_ARTIST)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
                val resolutionColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RESOLUTION)
                val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = Uri.withAppendedPath(
                        if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )

                    val localFile = LocalFileData(
                        id = id,
                        title = cursor.getString(titleColumn),
                        uri = contentUri,
                        mimeType = cursor.getString(mimeTypeColumn),
                        size = cursor.getLong(sizeColumn),
                        duration = cursor.getLong(durationColumn),
                        artist = cursor.getString(artistColumn),
                        album = cursor.getString(albumColumn),
                        year = cursor.getInt(yearColumn),
                        genre = cursor.getString(genreColumn),
                        composer = cursor.getString(composerColumn),
                        albumArtist = cursor.getString(albumArtistColumn),
                        width = if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) null else cursor.getInt(widthColumn),
                        height = if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) null else cursor.getInt(heightColumn),
                        resolution = if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) null else cursor.getString(resolutionColumn),
                        dateTaken = cursor.getLong(dateTakenColumn),
                        dateAdded = cursor.getLong(dateAddedColumn),
                        dateModified = cursor.getLong(dateModifiedColumn)
                    )
                    mediaFiles.add(localFile)
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