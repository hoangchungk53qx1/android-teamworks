package com.graduation.teamwork.models.firebase

import android.os.Parcelable
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.extensions.formatDateTime
import com.graduation.teamwork.extensions.toDate
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import kotlinx.android.parcel.Parcelize
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

@Parcelize
data class NotiDetailRoom(
    var idUserAction: String? = null,
    val category: String? = null,
    val content: String? = null,
    var idCategory: String? = null,
    var idUserAaffected: String? = null,
    val level: String? = null,
    val timestamp: Long
) : Parcelable {
    fun asInfoNoti(currentIdUser: String, users: List<DtUser>?, rooms: List<DtRoom>?): InfoNoti {
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

        var data: String = this.idCategory ?: ""
        // set room
        this.idCategory?.let { id ->
            data = rooms?.find { id == it._id }.toJson()
            this.idCategory = rooms?.find { id == it._id }?.name
        }

        val content = when {
            idUserAaffected != null -> {

                if (level != null) {
                    "$idUserAction ${this.content} $idUserAaffected trở thành $level trong phòng $idCategory"
                } else {
                    "$idUserAction ${this.content} $idUserAaffected vào phòng $idCategory"

                }
            }
            content?.contains("tạo") == true -> {
                "$idUserAction ${this.content} phòng $idCategory"
            }
            else -> {
                "$idUserAction ${this.content} vào công việc $idCategory"
            }
        }

        return InfoNoti(
            data,
            content,
            timestamp.toDate().formatDateTime(),
            timestamp,
            Constant.TYPE_NOTI.ROOM
        )
    }
}