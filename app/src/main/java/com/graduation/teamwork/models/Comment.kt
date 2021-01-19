package com.graduation.teamwork.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * com.graduation.teamwork.models
 * Created on 11/24/20
 */

@Parcelize
data class Comment (
    val createAt: Long? = null,
    val updateAt: Long? = null,
    @Json(name = "_id")
    val _id: String? = null,
    val content: String? = null,
    val username: String? = null,

    @Json(name = "__v")
    val v: Long? = null
) : Parcelable