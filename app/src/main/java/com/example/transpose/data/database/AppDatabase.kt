package com.example.transpose.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.transpose.data.database.dao.PlaylistDao
import com.example.transpose.data.database.dao.VideoDao
import com.example.transpose.data.database.entity.PlaylistEntity
import com.example.transpose.data.database.entity.VideoEntity

@Database(entities = [PlaylistEntity::class, VideoEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun videoDao(): VideoDao
}