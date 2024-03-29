package com.graduation.teamwork.domain.repository

import com.graduation.teamwork.data.remote.ApiServer
import com.graduation.teamwork.models.*
import com.graduation.teamwork.models.data.MemberInRoom
import io.reactivex.rxjava3.core.Single

class RemoteRepositoryImpl(
    private val apiServer: ApiServer
) : RemoteRepository {
    override fun getAllRoom(): Single<Room> = apiServer.queryAllRoom()
    override fun getAllMember(): Single<User> = apiServer.queryAllUser()
    override fun getAllTask(): Single<Task> = apiServer.queryAllTask()
    override fun getAllRoomForUser(idUser: String): Single<Room> = apiServer.queryRoomByUser(idUser)
    override fun getAllGroupForUser(idUser: String): Single<Group> =
        apiServer.queryGroupByUser(idUser)

    override fun getAllMemberInRoom(idRoom: String): Single<BaseModel> =
        apiServer.queryUserInRoom(idRoom)

    override fun getRoom(id: String): Single<Room> = apiServer.queryRoom(id)
    override fun addRoomInGroup(idGroup: String, nameRoom: String, idUser: String): Single<Group> =
        apiServer.addRoomInGroup(idGroup, nameRoom, idUser)

    override fun addSubtaskInTask(idTask: String, nameStage: String, idUser: String): Single<Task> =
        apiServer.addSubtaskInTask(idTask, nameStage, idUser)

    override fun addGroup(idUser: String, name: String): Single<Group> =
        apiServer.addGroup(idUser, name)

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

    override fun changeDeadline(id: String, deadline: Long, idUser: String): Single<Task> =
        apiServer.changeDeadline(id, deadline, idUser)

    override fun addMemberInTask(
        idTask: String,
        idUser: String,
        idUserAction: String
    ): Single<Task> = apiServer.addUserInTask(idTask, idUser, idUserAction)

    override fun queryMemberInTask(idTask: String): Single<Task> = apiServer.queryUserInTask(idTask)


}