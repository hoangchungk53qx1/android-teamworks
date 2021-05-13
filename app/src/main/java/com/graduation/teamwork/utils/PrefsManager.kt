package com.graduation.teamwork.utils

import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.extensions.toListObject
import com.graduation.teamwork.extensions.toObject
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.data.TaskListItem
import com.pixplicity.easyprefs.library.Prefs

class PrefsManager {

    fun saveTotalRoom(number: Int) {
        Prefs.putInt(Constant.PrefsKey.TOTAL_ROOM.value, number)
    }

    fun saveTotalGroup(number: Int) {
        Prefs.putInt(Constant.PrefsKey.TOTAL_GROUP.value, number)
    }

    fun getTotalRoom(): Int =
        Prefs.getInt(Constant.PrefsKey.TOTAL_ROOM.value, 0)

    fun getTotalGroup(): Int =
        Prefs.getInt(Constant.PrefsKey.TOTAL_GROUP.value, 0)

    fun saveLoadOnlyGroup(idGroup: String?) {
        Prefs.putString(Constant.PrefsKey.LOAD_ONLY_GROUP.value, idGroup)
    }

    fun getLoadOnlyGroup(): String? =
        Prefs.getString(Constant.PrefsKey.LOAD_ONLY_GROUP.value, null)

    fun getUser(): DtUser? =
        if (Prefs.getString(Constant.PrefsKey.USER.value, null) != null) {
            Prefs.getString(Constant.PrefsKey.USER.value, null).toObject()
        } else null

    fun saveUser(user: DtUser) {
        Prefs.putString(Constant.PrefsKey.USER.value, user.toJson())
    }

    fun saveRooms(rooms: List<DtRoom>) {
        Prefs.putString(Constant.PrefsKey.ALL_ROOM.value, rooms.toJson())
    }

    fun getRooms(): List<DtRoom>? =
        if (Prefs.getString(Constant.PrefsKey.ALL_ROOM.value, null) != null) {
            Prefs.getString(Constant.PrefsKey.ALL_ROOM.value, null).toListObject()
        } else null

    fun saveTasks(tasks: List<TaskListItem.DtTask>) {
        Prefs.putString(Constant.PrefsKey.ALL_TASK.value, tasks.toJson())
    }

    fun getTasks(): List<TaskListItem.DtTask>? =
        if (Prefs.getString(Constant.PrefsKey.ALL_TASK.value, null) != null) {
            Prefs.getString(Constant.PrefsKey.ALL_TASK.value, null).toListObject()
        } else null


    fun saveUsers(users: List<DtUser>) {
        Prefs.putString(Constant.PrefsKey.ALL_USER.value, users.toJson())
    }

    fun getUsers(): List<DtUser>? =
        if (Prefs.getString(Constant.PrefsKey.ALL_USER.value, null) != null) {
            Prefs.getString(Constant.PrefsKey.ALL_USER.value, null).toListObject()
        } else null

    fun clearUser() {
        Prefs.putString(Constant.PrefsKey.USER.value, null)
    }

    fun savePkgList(pkgName: String?) {
        Prefs.putString(Constant.PrefsKey.PKG_LIST.value, pkgName)
    }

    fun getPkgList(): String? {
        return Prefs.getString(Constant.PrefsKey.PKG_LIST.value, null)
    }

    fun saveIsFirstTime(isFirstTime: Boolean) {
        Prefs.putBoolean(Constant.PrefsKey.IS_FIRST_TIME.value, isFirstTime)
    }

    fun getIsFirstTime(): Boolean {
        return Prefs.getBoolean(Constant.PrefsKey.IS_FIRST_TIME.value, true)
    }

    fun setMode(mode: Int) {
        Prefs.putInt(Constant.PrefsKey.MODE.value, mode)
    }

    fun getMode(): Int {
        return Prefs.getInt(Constant.PrefsKey.MODE.value, 0)
    }

    fun setChange(isChange: Boolean) {
        Prefs.putBoolean(Constant.PrefsKey.IS_CHANGE.value, isChange)
    }

    fun isChange(): Boolean {
        return Prefs.getBoolean(Constant.PrefsKey.IS_CHANGE.value, false)
    }
}