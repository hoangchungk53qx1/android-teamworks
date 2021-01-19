package com.graduation.teamwork.utils.permission

interface PermissionManager {
    fun hasReadStorage(): Boolean

    fun isDefaultCall(): Boolean

    fun hasReadSms(): Boolean

    fun hasSendSms(): Boolean

    fun hasContacts(): Boolean

    fun hasPhone(): Boolean

    fun hasCalling(): Boolean

    fun hasStorage(): Boolean

    fun hasReadCallLog() : Boolean
}