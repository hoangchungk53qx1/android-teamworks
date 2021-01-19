package com.graduation.teamwork.utils

import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.extensions.fromJson
import com.graduation.teamwork.extensions.fromObject
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.data.TaskListItem
import com.pixplicity.easyprefs.library.Prefs

class PrefsManager {

    fun saveTotalRoom(number: Int) {
        Prefs.putInt(Constant.PREFS.TOTAL_ROOM.value, number)
    }

    fun saveTotalGroup(number: Int) {
        Prefs.putInt(Constant.PREFS.TOTAL_GROUP.value, number)
    }

    fun getTotalRoom(): Int =
        Prefs.getInt(Constant.PREFS.TOTAL_ROOM.value, 0)

    fun getTotalGroup(): Int =
        Prefs.getInt(Constant.PREFS.TOTAL_GROUP.value, 0)

    fun saveLoadOnlyGroup(idGroup: String?) {
        Prefs.putString(Constant.PREFS.LOAD_ONLY_GROUP.value, idGroup)
    }

    fun getLoadOnlyGroup(): String? =
        Prefs.getString(Constant.PREFS.LOAD_ONLY_GROUP.value, null)

    fun getUser(): DtUser? =
        if (Prefs.getString(Constant.PREFS.USER.value, null) != null) {
            Prefs.getString(Constant.PREFS.USER.value, null).fromObject()
        } else null

    fun saveUser(user: DtUser) {
        Prefs.putString(Constant.PREFS.USER.value, user.toJson())
    }

    fun saveRooms(rooms: List<DtRoom>) {
        Prefs.putString(Constant.PREFS.ALL_ROOM.value, rooms.toJson())
    }

    fun getRooms(): List<DtRoom>? =
        if (Prefs.getString(Constant.PREFS.ALL_ROOM.value, null) != null) {
            Prefs.getString(Constant.PREFS.ALL_ROOM.value, null).fromJson()
        } else null

    fun saveTasks(tasks: List<TaskListItem.DtTask>) {
        Prefs.putString(Constant.PREFS.ALL_TASK.value, tasks.toJson())
    }

    fun getTasks(): List<TaskListItem.DtTask>? =
        if (Prefs.getString(Constant.PREFS.ALL_TASK.value, null) != null) {
            Prefs.getString(Constant.PREFS.ALL_TASK.value, null).fromJson()
        } else null


    fun saveUsers(users: List<DtUser>) {
        Prefs.putString(Constant.PREFS.ALL_USER.value, users.toJson())
    }

    fun getUsers(): List<DtUser>? =
        if (Prefs.getString(Constant.PREFS.ALL_USER.value, null) != null) {
            Prefs.getString(Constant.PREFS.ALL_USER.value, null).fromJson()
        } else null

    fun clearUser() {
        Prefs.putString(Constant.PREFS.USER.value, null)
    }

}