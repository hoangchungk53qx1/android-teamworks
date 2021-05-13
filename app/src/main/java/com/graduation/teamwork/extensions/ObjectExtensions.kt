package com.graduation.teamwork.extensions

import com.graduation.teamwork.models.DtUser

fun List<DtUser>.intersectUser(list: List<DtUser>): List<DtUser> {
    val result = mutableListOf<DtUser>()

    this.forEach {
        if (list.map { it._id }.contains(it._id)) {
            result.add(it)
        }
    }

    return result
}