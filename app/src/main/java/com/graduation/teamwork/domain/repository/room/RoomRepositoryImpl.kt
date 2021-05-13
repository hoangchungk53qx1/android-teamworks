package com.graduation.teamwork.domain.repository.room

import com.graduation.teamwork.data.remote.ApiServer
import com.graduation.teamwork.models.*
import io.reactivex.rxjava3.core.Single

/**
 * com.graduation.teamwork.domain.repository
 * Created on 11/24/20
 */

class RoomRepositoryImpl(
    private val apiServer: ApiServer
) : RoomRepository {
    override fun getAllRoom(): Single<Room> = apiServer.queryAllRoom()
    override fun getAllRoomByGroup(idGroup: String): Single<Room> =
        apiServer.queryRoomInGroup(idGroup)
    override fun getRoom(id: String): Single<Room> = apiServer.queryRoom(id)
    override fun getRoomByUser(idUser: String): Single<Room> = apiServer.queryRoomByUser(idUser)
    override fun addRoom(name: String, idUser: String): Single<Room> =
        apiServer.addRoom(name, idUser)
    override fun addStageInRoom(idRoom: String, nameStage: String, idUser: String): Single<Room> =
        apiServer.addStageInRoom(idRoom, nameStage, idUser)
    override fun addTaskInStage(id: String, name: String): Single<Stage> =
        apiServer.addTaskInStage(id, name)

}