package com.graduation.teamwork.domain.repository.group

import com.graduation.teamwork.models.Group
import com.graduation.teamwork.models.Room
import io.reactivex.rxjava3.core.Single

interface GroupRepository {
    fun getAllGroup(): Single<Group>
    fun getAllGroupByUser(idUser: String): Single<Group>
    fun getAllRoomInGroup(idGroup: String): Single<Room>
    fun addGroup(idUser: String, name: String): Single<Group>
    fun addRoomInGroup(idUser: String, nameRoom: String, idGroup: String): Single<Group>
}