package com.graduation.teamwork.data.local

import androidx.room.TypeConverter
import java.util.*


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(value) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}