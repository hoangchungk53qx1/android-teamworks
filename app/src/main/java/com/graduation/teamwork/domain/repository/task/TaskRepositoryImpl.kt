package com.graduation.teamwork.domain.repository.task

import com.graduation.teamwork.data.remote.ApiServer
import com.graduation.teamwork.models.Room
import com.graduation.teamwork.models.Stage
import com.graduation.teamwork.models.User
import io.reactivex.rxjava3.core.Single

class TaskRepositoryImpl(
    private val apiServer: ApiServer
): TaskRepository {
    override fun addTaskInStage(id: String, name: String): Single<Stage> = apiServer.addTaskInStage(id, name)
    override fun addStageInRoom(id: String, name: String, idUser: String): Single<Room> = apiServer.addStageInRoom(id, name, idUser)

    override fun getAllUser(): Single<User> = apiServer.queryAllUser()

    override fun addUserInRoom(
        idRoom: String,
        idUserAction: String,
        idUserAdded: String
    ): Single<Room> = apiServer.addUserInRoom(idRoom, idUserAction, idUserAdded)

    override fun deleteUserInRoom(
        idRoom: String,
        idUser: String,
        idUserWillDeleted: String
    ): Single<Room> = apiServer.deleteUserInRoom(idRoom, idUser, idUserWillDeleted)

    override fun setLevel(
        idRoom: String,
        idUser: String,
        idUserWillSet: String,
        level: Int
    ): Single<Room> = apiServer.setLevel(idRoom, idUser, idUserWillSet, level)
}