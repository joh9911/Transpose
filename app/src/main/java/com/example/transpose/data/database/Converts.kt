package com.example.transpose.data.database

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.stream.StreamType

// 3. TypeConverter
class Converters {
    @TypeConverter
    fun fromImageList(value: List<Image>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toImageList(value: String): List<Image> {
        val listType = object : TypeToken<List<Image>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromStreamType(value: StreamType): String {
        return value.name
    }

    @TypeConverter
    fun toStreamType(value: String): StreamType {
        return StreamType.valueOf(value)
    }
}