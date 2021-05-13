package com.graduation.teamwork.models

import android.os.Parcelable
import com.graduation.teamwork.models.data.MemberInRoom
import com.graduation.teamwork.models.data.TaskListItem
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * com.graduation.teamwork.models
 * Created on 11/19/20
 */

@Parcelize
data class Room(
    val message: String? = null,
    val totalResult: Long? = null,

//    @Embedded
//    @TypeConverters(RoomConverters::class)
    val data: List<DtRoom>? = null
) : Parcelable

@Parcelize
data class DtRoom(
    val isHighlight: Boolean? = null,
    val modify: Long? = null,
    val isEnableComment: Boolean? = null,
    val isEnableAdded: Boolean? = null,
    val isFollowing: Boolean? = null,
    val createAt: Long? = null,
    val updateAt: Long? = null,
    val image: String? = null,
    val label: Int? = null,
    val name: String? = null,

    @Json(name = "_id")
    val _id: String? = null,
    val users: List<UserInRoom>? = null,
    val deadline: Long? = null,
    val stages: List<DtStage?>? = null,

    @Json(name = "__v")
    val __v: Long? = null
) : Parcelable {
    fun asHeader(): TaskListItem.Header {
        return TaskListItem.Header(
            isHighlight,
            modify,
            isEnableComment,
            isEnableAdded,
            isFollowing,
            createAt,
            updateAt,
            image,
            label,
            name,
            _id,
            users,
            deadline,
            stages,
            __v
        )
    }

    fun getMemberInRooms(): List<MemberInRoom>? {
        val idRoom = _id
        return users?.map {
            MemberInRoom(it._id, idRoom, it.level, it.user)
        }
    }
}

@Parcelize
data class UserInRoom(
    val level: Long? = null,
    @Json(name = "_id")
    val _id: String? = null,
    val user: DtUser? = null
) : Parcelable