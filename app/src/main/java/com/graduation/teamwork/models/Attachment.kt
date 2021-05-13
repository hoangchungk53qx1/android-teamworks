package com.graduation.teamwork.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * com.graduation.teamwork.models
 * Created on 11/24/20
 */

/**
 * type of  attachment
 * 0: image
 * 1: file
 * 2: link
 */
enum class TypeAttachment(val value: String) {
   IMAGE("image"),
   LINK("link")
}

@Parcelize
data class Attachment(
    val createAt: Long? = null,
    val updateAt: Long? = null,

    @Json(name = "_id")
    val _id: String? = null,
    val type: String? = null,
    val data: Data? = null,
    val idTask: String? = null,

    @Json(name = "__v")
    val v: Long? = null
) : Parcelable

@Parcelize
data class Data(
    val name: String,
    val url: String
) : Parcelable
