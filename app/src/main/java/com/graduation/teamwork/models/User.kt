package com.graduation.teamwork.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonQualifier
import kotlinx.android.parcel.Parcelize

/**
 * com.graduation.teamwork.models
 * Created on 11/24/20
 */

@Parcelize
data class User(
        val message: String? = null,
        val totalResult: Long? = null,
        val data: List<DtUser>? = null
) : Parcelable

@Parcelize
data class DtUser(
        val status: Boolean? = null,
        val idGroup: List<String?>? = null,
        val idRoom: List<String?>? = null,
        val createAt: Long? = null,
        val updateAt: Long? = null,

        @Json(name = "_id")
        val _id: String? = null,
        val fullname: String? = null,
        val password: String? = null,
        val mail: String? = null,
        val city: String? = null,
        val numberphone: String? = null,
        val image: ImageClass? = null,
        var isSelected: Boolean = false,

        @Json(name = "__v")
        val __v: Long? = null
) : Parcelable

@Parcelize
data class ImageClass(
        val name: String? = null,
        val url: String? = null
) : Parcelable
