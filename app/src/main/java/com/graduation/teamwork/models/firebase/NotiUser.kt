package com.graduation.teamwork.models.firebase

import android.os.Parcelable
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.extensions.formatDateTime
import com.graduation.teamwork.extensions.toDate
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotiDetailUser(
    val idUserPerformer: String? = null,
    val category: String? = null,
    val content: String? = null,
    val timestamp: Long
) : Parcelable {
    fun asInfoNoti(): InfoNoti {
        return InfoNoti(
            idUserPerformer ?: "",
            "Bạn $content",
            timestamp.toDate().formatDateTime(),
            timestamp,
            Constant.NotifyType.USER
        )
    }
}