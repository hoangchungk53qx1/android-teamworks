package com.graduation.teamwork.data.room

import androidx.room.TypeConverter
import java.util.*


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(value) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
//
//class StageConverters {
//    @TypeConverter
//    fun fromDtStageList(data: List<DtStage>?): String? {
//        if (data == null) {
//            return null
//        }
//
//        val moshi = Moshi.Builder().build()
//        val type = Types.newParameterizedType(List::class.java, DtStage::class.java)
//        val adapter = moshi.adapter<List<DtStage>>(type)
//
//        return adapter.toJson(data)
//    }
//
//    @TypeConverter
//    fun toDtStageList(data: String): List<DtStage>? {
//        return data.fromJson()
//    }
//}
//
//class TaskConverters {
//    @TypeConverter
//    fun fromDtStageList(data: List<DtStage>?): String? {
//        if (data == null) {
//            return null
//        }
//
//        val moshi = Moshi.Builder().build()
//        val type = Types.newParameterizedType(List::class.java, DtStage::class.java)
//        val adapter = moshi.adapter<List<DtStage>>(type)
//
//        return adapter.toJson(data)
//    }
//
//    @TypeConverter
//    fun toDtStageList(data: String): List<DtStage>? {
//        return data.fromJson()
//    }
//}
//
//class RoomConverters {
//    @TypeConverter
//    fun fromDtRoomList(data: List<DtRoom>?): String? {
//        if (data == null) {
//            return null
//        }
//
//        val moshi = Moshi.Builder().build()
//        val type = Types.newParameterizedType(List::class.java, DtRoom::class.java)
//        val adapter = moshi.adapter<List<DtRoom>>(type)
//
//        return adapter.toJson(data)
//    }
//
//    @TypeConverter
//    fun toDtRoomList(data: String): List<DtRoom>? {
//        return data.fromJson()
//    }
//}
//
//class DtRoomConverters {
//    @TypeConverter
//    fun fromStageList(data: List<Stage>?): String? {
//        if (data == null) {
//            return null
//        }
//
//        val moshi = Moshi.Builder().build()
//        val type = Types.newParameterizedType(List::class.java, Stage::class.java)
//        val adapter = moshi.adapter<List<Stage>>(type)
//
//        return adapter.toJson(data)
//    }
//
//    @TypeConverter
//    fun toStageList(data: String): List<Stage>? {
//        return data.fromJson()
//    }
//
//    @TypeConverter
//    fun fromStringList(data: List<String>?): String? {
//        if (data == null) {
//            return null
//        }
//
//        val moshi = Moshi.Builder().build()
//        val type = Types.newParameterizedType(List::class.java, String::class.java)
//        val adapter = moshi.adapter<List<String>>(type)
//
//        return adapter.toJson(data)
//    }
//
//    @TypeConverter
//    fun toStringList(data: String): List<String>? {
//        return data.fromJson()
//    }
//}
