package com.graduation.teamwork.data.local

import com.graduation.teamwork.R

/**
 * com.graduation.teamwork.data.local
 * Created on 11/19/20
 */

class Constant {
    enum class IntentKey(val value: String) {
        ROOM("IT_ROOM"),
        ID_ROOM("ID_ROOM"),
        STAGE("IT_STAGE"),
        USERS("IT_USER"),
        DETAIL_TASK("IT_DETAIL_TASK"),
        DETAIL_USER("IT_DETAIL_USER")

    }

    enum class LevelKey(val value: Long) {
        CREATED(0L),
        MANAGER(1L),
        MEMBER(2L)
    }

    enum class PrefsKey(val value: String) {
        USER("PREFS_USER"),
        GROUP("PREFS_GROUP"),
        ROOM("PREFS_ROOM"),
        TASK("PREFS_TASK"),
        ALL_TASK("PREFS_ALL_TASK"),
        ALL_ROOM("PREFS_ALL_ROOM"),
        ALL_USER("PREFS_ALL_USER"),
        LOAD_ONLY_GROUP("PREFS_LOAD_ONLY_GROUP"),
        TOTAL_ROOM("PREFS_TOTAL_ROOM"),
        TOTAL_GROUP("PREFS_TOTAL_GROUP"),
        PKG_LIST("PKG_LIST"),
        IS_FIRST_TIME("IS_FIRST_TIME"),
        MODE("MODE"),
        IS_CHANGE("IS_CHANGE")
    }

    enum class ErrorTag(val value: String) {
        USER("ERR_USER"),
        ROOM("ERR_ROOM"),
        TASK("ERR_TASK"),
        GROUP("ERR_GROUP"),
    }

    enum class NotifyType() {
        USER, ROOM, TASK
    }

    interface NotifyKey {
        companion object {
            const val TOAST_MESSAGE = "TOAST_MESSAGE"
            const val CHANEL_ID = "CHANEL_ID"
            const val CHANEL_NAME = "Teamwork"
            const val CHANEL_DESCRIPTION = "Teamwork notify"
            const val NOTIFICATION_ID = 1
            const val WORKER_NAME = "com.graduation.teamwork.worker"
            const val TITLE = "TITLE"
            const val MESSAGE = "MESSAGE"
            const val ENABLE_NOTIFY = "ENABLE_NOTIFY"
        }
    }

    enum class LabelColor(val value: Int) {
        RED(0),
        ORANGE(1),
        YELLOW(2),
        OLIVE_GREEN(3),
        GREEN(4),
        MIN_GREEN(5),
        SKY_BLUE(6),
        BLUE(7),
        VIOLET(8),
        MAGENTA(9);

        companion object {
            fun fromInt(value: Int) = LabelColor.values().first { it.value == value }
            fun toColor(value: LabelColor) = when (value) {
                RED -> R.color.label_red
                ORANGE -> R.color.label_orange
                OLIVE_GREEN -> R.color.label_oliveGreen
                GREEN -> R.color.label_green
                MIN_GREEN -> R.color.label_minGreen
                SKY_BLUE -> R.color.label_skyBlue
                BLUE -> R.color.label_blue
                VIOLET -> R.color.label_violet
                MAGENTA -> R.color.label_magenta
                else -> R.color.label_grey
            }

            fun toColor(value: Int) = toColor(fromInt(value))

            fun allColors() = listOf(
                RED,
                ORANGE,
                YELLOW,
                OLIVE_GREEN,
                GREEN,
                MIN_GREEN,
                SKY_BLUE,
                BLUE,
                VIOLET,
                MAGENTA
            )
        }
    }
    enum class TrackKey(val value: String){
        APP("App"),
        NETWORK("Network"),
        TODAY("Today"),
        YESTERDAY("Yesterday"),
        WEEK("Week"),
        MONTH("Month")
    }

    enum class SheetType(val value: String) {
        MEMBER("MEMBER"),
        LABEL("LABEL"),
        NONE("NONE")
    }

}
