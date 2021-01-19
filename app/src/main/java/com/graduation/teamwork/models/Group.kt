package com.graduation.teamwork.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group (
    val message: String,
    val totalResult: Long,
    val data: List<DtGroup>
) : Parcelable

@Parcelize
data class DtGroup (
    val createAt: Long,
    val updateAt: Long,
    val _id: String,
    val idUser: String,
    val name: String,
    val rooms: List<DtRoom>,
    val v: Long
) : Parcelable