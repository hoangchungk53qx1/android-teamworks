package com.graduation.teamwork.models.data

import android.os.Parcelable
import com.graduation.teamwork.models.*
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize


sealed class TaskListItem {
    @Parcelize
    data class DtTask(
        val labels: List<Int>? = null,
        val description: String? = null,
        val isCompleted: Boolean? = null,
        val createAt: Long? = null,
        val updateAt: Long? = null,
        val deadline: Long = 0,

        @Json(name = "_id")
        val _id: String? = null,

        val subtasks: List<DtSubtask>? = null,
        val attachments: List<Attachment>? = null,
        val comments: List<Comment>? = null,
        val users: List<DtUser>? = null,
        val name: String? = null,
        val idStage: String? = null,

        @Json(name = "__v")
        val v: Long? = null
    ) : TaskListItem(), Parcelable

    @Parcelize
    data class Header(
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

        val _id: String? = null,
        val users: List<UserInRoom>? = null,
        val deadline: Long? = null,
        val stages: List<DtStage?>? = null,

        val __v: Long? = null
    ) : TaskListItem(), Parcelable {
        fun asRoom(): DtRoom {
            return DtRoom(
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
    }

}