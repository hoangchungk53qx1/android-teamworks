package com.graduation.teamwork.models

import android.os.Parcelable
import com.graduation.teamwork.models.data.TaskListItem
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * com.graduation.teamwork.models
 * Created on 11/21/20
 */

@Parcelize
//@Entity(tableName = Constant.TABLE_ROOM.STAGE)
data class Stage (
    val message: String? = null,
    val totalResult: Long? = null,

//    @Embedded
//    @TypeConverters(StageConverters::class)
    val data: List<DtStage>? = null
) : Parcelable

@Parcelize
data class DtStage (
    val createAt: Long? = null,
    val updateAt: Long? = null,

    @Json(name = "_id")
    val _id: String? = null,

    var tasks: List<TaskListItem.DtTask>? = null,
    val name: String? = null,
    val idRoom: String? = null,

    @Json(name = "__v")
    val v: Long? = null
) : Parcelable