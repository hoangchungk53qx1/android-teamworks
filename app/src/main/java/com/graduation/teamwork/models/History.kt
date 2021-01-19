package com.graduation.teamwork.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * com.graduation.teamwork.models
 * Created on 11/24/20
 */
@Parcelize
data class History(
    val message: String? = null,
    val totalResult: Long? = null,
    val data: List<DtHistory>? = null
) : Parcelable

@Parcelize
data class DtHistory(
    val category: String? = null,
    val timestamp: Long? = null,
    val content: String? = null,
    val createAt: Long? = null,
    val updateAt: Long? = null,
) : Parcelable


@Parcelize
data class HistoryType(
    val category: String? = null,
    val idCategory: String? = null
) : Parcelable
