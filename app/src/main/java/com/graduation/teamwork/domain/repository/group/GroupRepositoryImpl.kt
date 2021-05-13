package com.graduation.teamwork.domain.repository.group

import com.graduation.teamwork.data.remote.ApiServer
import com.graduation.teamwork.models.Group
import com.graduation.teamwork.models.Room
import io.reactivex.rxjava3.core.Single

class GroupRepositoryImpl(private val apiServer: ApiServer): GroupRepository {
    override fun getAllGroup(): Single<Group> = apiServer.queryAllGroup()
    override fun getAllGroupByUser(idUser: String): Single<Group> = apiServer.queryGroupByUser(idUser)
    override fun getAllRoomInGroup(idGroup: String): Single<Room> = apiServer.queryRoomInGroup(idGroup)
    override fun addGroup(idUser: String, name: String): Single<Group> = apiServer.addGroup(idUser, name)
    override fun addRoomInGroup(idUser: String, nameRoom: String, idGroup: String): Single<Group> = apiServer.addRoomInGroup(idUser, nameRoom, idGroup)
}