package com.graduation.teamwork.domain.repository.task

import com.graduation.teamwork.models.Room
import com.graduation.teamwork.models.Stage
import com.graduation.teamwork.models.User
import io.reactivex.rxjava3.core.Single

interface TaskRepository {
    fun addTaskInStage(id: String, name: String): Single<Stage>
    fun addStageInRoom(id: String, name: String, idUser: String): Single<Room>
    fun getAllUser(): Single<User>
    fun addUserInRoom(idRoom: String, idUserAction: String, idUserAdded: String): Single<Room>
    fun deleteUserInRoom(idRoom: String, idUser: String, idUserWillDeleted: String): Single<Room>
    fun setLevel(idRoom: String, idUser: String, idUserWillSet: String, level: Int): Single<Room>

    fun getRoomWith(id: String): Single<Room>
}