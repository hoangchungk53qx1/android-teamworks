package com.graduation.teamwork.models

import android.os.Parcelable
import com.graduation.teamwork.models.data.TaskListItem
import kotlinx.android.parcel.Parcelize

/**
 * com.graduation.teamwork.models
 * Created on 11/24/20
 */

@Parcelize
data class Task (
    val message: String? = null,
    val totalResult: Long? = null,
    val data: List<TaskListItem.DtTask>? = null
) : Parcelable

