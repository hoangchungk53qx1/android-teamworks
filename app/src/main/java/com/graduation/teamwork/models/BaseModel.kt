package com.graduation.teamwork.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * com.graduation.teamwork.models
 * Created on 11/24/20
 */

@Parcelize
open class BaseModel(
    val message: String? = null,
    val totalResult: Long? = null,
    val data: List<UserInRoom>? = null
) : Parcelable