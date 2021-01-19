package com.graduation.teamwork.models.firebase

import android.os.Parcelable
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.extensions.formatDateTime
import com.graduation.teamwork.extensions.toDate
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.data.TaskListItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotiDetailTask(
    var idUserAaffected: String? = null,
    var idUserAction: String? = null,
    val category: String? = null,
    val content: String? = null,
    var idCategory: String? = null,
    val timestamp: Long
) : Parcelable {
    fun asInfoNoti(
        currentIdUser: String,
        users: List<DtUser>?,
        tasks: List<TaskListItem.DtTask>?
    ): InfoNoti {
        // set name
        if (currentIdUser == idUserAction) {
            this.idUserAction = "bạn"
        } else if (currentIdUser == idUserAaffected) {
            this.idUserAaffected = " bạn "
        }

        if (idUserAction?.trim() != "bạn") {
            this.idUserAction = users?.find { (idUserAction == it._id) }?.fullname
        }

        if (idUserAaffected?.trim() != "bạn") {
            this.idUserAaffected?.let { id ->
                this.idUserAaffected = users?.find { (id == it._id) }?.fullname
            }
        }

        //set task
        var data: String = this.idCategory ?: ""
        this.idCategory?.let { id ->
            data = tasks?.find { id == it._id }.toJson()
            this.idCategory = tasks?.find { id == it._id }?.name
        }

        val content: String = if (idUserAaffected != null) {
            "$idUserAction ${this.content} $idUserAaffected vào công việc $idCategory"

        } else {
            "$idUserAction ${this.content} vào công việc $idCategory"
        }

        return InfoNoti(
            data,
            content,
            timestamp.toDate().formatDateTime(),
            timestamp,
            Constant.TYPE_NOTI.TASK
        )
    }
}