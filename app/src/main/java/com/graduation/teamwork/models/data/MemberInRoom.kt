package com.graduation.teamwork.models.data

import android.os.Parcelable
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.UserInRoom
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MemberInRoom(
    val id: String? = null,
    val idRoom: String? = null,
    val level: Long? = null,
    val user: DtUser? = null
) : Parcelable {
    fun asDtUser(): UserInRoom = UserInRoom(
        _id = id, user = user, level = level
    )
}