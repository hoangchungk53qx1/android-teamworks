package com.graduation.teamwork.domain.repository.room

import com.graduation.teamwork.models.Group
import com.graduation.teamwork.models.Room
import com.graduation.teamwork.models.Stage
import com.graduation.teamwork.models.User
import io.reactivex.rxjava3.core.Single

/**
 * com.graduation.teamwork.domain
 * Created on 11/24/20
 */

interface RoomRepository {
    fun getAllRoom(): Single<Room>
    fun getAllRoomByGroup(idGroup: String) : Single<Room>
    fun getRoom(id: String): Single<Room>
    fun getRoomByUser(idUser: String) : Single<Room>
    fun addRoom(name: String, idUser: String): Single<Room>

    fun addStageInRoom(idRoom: String, nameStage: String, idUser: String): Single<Room>

    fun addTaskInStage(id: String, name: String) : Single<Stage>
}
