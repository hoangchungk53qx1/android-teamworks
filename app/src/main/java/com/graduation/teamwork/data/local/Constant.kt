package com.graduation.teamwork.data.local

/**
 * com.graduation.teamwork.data.local
 * Created on 11/19/20
 */

class Constant {
    enum class INTENT(val value: String) {
        ROOM("IT_ROOM"),
        STAGE("IT_STAGE"),
        USERS("IT_USER"),
        DETAIL_TASK("IT_DETAIL_TASK"),
        DETAIL_USER("IT_DETAIL_USER")

    }

    enum class LEVEL(val value: Long) {
        CREATED(0L),
        MANAGER(1L),
        MEMBER(2L)
    }

    enum class DEFAULT(val value: Any) {
        ID_USER_DEFAULT("5fc3d511e22d8a65537c1f87")
    }

    enum class PREFS(val value: String) {
        USER("PREFS_USER"),
        GROUP("PREFS_GROUP"),
        ROOM("PREFS_ROOM"),
        TASK("PREFS_TASK"),
        ALL_TASK("PREFS_ALL_TASK"),
        ALL_ROOM("PREFS_ALL_ROOM"),
        ALL_USER("PREFS_ALL_USER"),
        LOAD_ONLY_GROUP("PREFS_LOAD_ONLY_GROUP"),
        TOTAL_ROOM("PREFS_TOTAL_ROOM"),
        TOTAL_GROUP("PREFS_TOTAL_GROUP")
    }

    enum class ERROR_TAG(val value: String) {
        USER("ERR_USER"),
        ROOM("ERR_ROOM"),
        TASK("ERR_TASK"),
        GROUP("ERR_GROUP"),
    }

    enum class TYPE_NOTI() {
        USER, ROOM, TASK
    }

    class TABLE_ROOM(val value: String) {
        companion object {
            const val RROOM = "RROOM"
            const val DTROOM = "RDTROOM"
            const val STAGE = "RSTAGE"
        }
    }

    interface NOTIFY {
        companion object {
            const val TOAST_MESSAGE = "TOAST_MESSAGE"
            const val CHANEL_ID = "CHANEL_ID"
            const val CHANEL_NAME = "Minecraftmod"
            const val CHANEL_DESCRIPTION = "Minecraftmod notify"
            const val NOTIFICATION_ID = 1
            const val WORKER_NAME = "com.minecraft.mcpeaddons.worker"
            const val TITLE = "TITLE"
            const val MESSAGE = "MESSAGE"
            const val ENABLE_NOTIFY = "ENABLE_NOTIFY"
        }
    }

}
