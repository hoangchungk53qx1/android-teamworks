package com.graduation.teamwork.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * com.graduation.teamwork.models
 * Created on 11/24/20
 */

@Parcelize
data class Subtask (
    val message: String? = null,
    val totalResult: Long? = null,
    val data: List<DtSubtask>? = null
) : Parcelable

@Parcelize
data class DtSubtask (
    val isCompleted: Boolean? = null,
    val createAt: Long? = null,
    val updateAt: Long? = null,

    @Json(name = "_id")
    val _id: String? = null,

    val idTask: String? = null,
    val name: String? = null,

    @Json(name = "__v")
    val v: Long? = null
) : Parcelable
