package com.graduation.teamwork.data.remote

import com.graduation.teamwork.models.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody

class ApiServerImpl(private val apiServer: ApiServer) : ApiServer {
    override fun queryAllUser(): Single<User> = apiServer.queryAllUser()
    override fun queryUserById(id: String): Single<User> = apiServer.queryUserById(id)

    override fun queryUserByName(username: String): Single<User> =
        apiServer.queryUserByName(username)

    override fun addUser(username: String, password: String, mail: String): Single<User> =
        apiServer.addUser(username, password, mail)

    override fun updateUser(
        id: String,
        numberphone: String,
        city: String,
        mail: String
    ): Single<User> = apiServer.updateUser(id, numberphone, city, mail)

    override fun updatePasswored(
        id: String,
        oldPassword: String,
        newPassword: String
    ): Single<User> = apiServer.updatePasswored(id, oldPassword, newPassword)

    override fun updateAvatar(id: String, part: MultipartBody.Part): Single<User> =
        apiServer.updateAvatar(id, part)

    //    override fun updateAvatar(id: String, image: RequestBody) = apiServer.updateAvatar(id, image);
    override fun loginWithUsername(username: String, password: String): Single<User> =
        apiServer.loginWithUsername(username, password)

    override fun loginWithMail(mail: String, password: String): Single<User> =
        apiServer.loginWithMail(mail, password)

    override fun logout(id: String): Completable = apiServer.logout(id)
    override fun queryAllGroup(): Single<Group> = apiServer.queryAllGroup()

    override fun queryGroup(id: String): Single<Group> = apiServer.queryGroup(id)

    override fun queryGroupByUser(idUser: String): Single<Group> =
        apiServer.queryGroupByUser(idUser)

    override fun queryRoomInGroup(idGroup: String): Single<Room> =
        apiServer.queryRoomInGroup(idGroup)

    override fun addGroup(idUser: String, name: String): Single<Group> =
        apiServer.addGroup(idUser, name)

    override fun addRoomInGroup(idGroup: String, nameRoom: String, idUser: String): Single<Group> =
        apiServer.addRoomInGroup(idGroup, nameRoom, idUser)

    override fun queryAllRoom(): Single<Room> = apiServer.queryAllRoom()

    override fun queryRoom(id: String): Single<Room> = apiServer.queryRoom(id)
    override fun queryRoomByUser(idUser: String): Single<Room> = apiServer.queryRoomByUser(idUser)

    override fun queryStageInRoom(idRoom: String): Single<Room> = apiServer.queryStageInRoom(idRoom)

    override fun queryUserInRoom(idRoom: String): Single<BaseModel> = apiServer.queryUserInRoom(idRoom)

    override fun addRoom(
        name: String, idUser: String
    ): Single<Room> = apiServer.addRoom(name, idUser)

    override fun addStageInRoom(idRoom: String, nameStage: String, idUser: String): Single<Room> =
        apiServer.addStageInRoom(idRoom, nameStage, idUser)

    override fun deleteStageInRoom(idRoom: String, idStage: String): Single<Room> =
        apiServer.deleteStageInRoom(idRoom, idStage)

    override fun addUserInRoom(
        idRoom: String,
        idUserAction: String,
        idUserAdded: String
    ): Single<Room> =
        apiServer.addUserInRoom(idRoom, idUserAction, idUserAdded)

    override fun deleteUserInRoom(
        idRoom: String,
        idUser: String,
        idUserWillDeleted: String
    ): Single<Room> =
        apiServer.deleteUserInRoom(idRoom, idUser, idUserWillDeleted)

    override fun setLevel(
        idRoom: String,
        idUser: String,
        idUserWillSet: String,
        level: Int
    ): Single<Room> =
        apiServer.setLevel(idRoom, idUser, idUserWillSet, level)

    override fun queryAllStage(): Single<Stage> = apiServer.queryAllStage()

    override fun addTaskInStage(id: String, name: String): Single<Stage> =
        apiServer.addTaskInStage(id, name)

    override fun queryAllTask(): Single<Task> = apiServer.queryAllTask()

    override fun addSubtaskInTask(idTask: String, nameSubtask: String, idUser: String) =
        apiServer.addSubtaskInTask(idTask, nameSubtask, idUser)

    override fun uploadImageTask(
        id: String,
        part: MultipartBody.Part,
        idUser: String
    ): Single<Task> = apiServer.uploadImageTask(id, part, idUser)

    override fun addUserInTask(idTask: String, idUser: String, idUserAction: String): Single<Task> =
        apiServer.addUserInTask(idTask, idUser, idUserAction)

    override fun deleteUserInTask(
        idTask: String,
        idCategory: String,
        idUserAction: String
    ): Single<Task> = apiServer.deleteUserInTask(idTask, idCategory, idUserAction)

    override fun addLinkTask(idTask: String, link: String, idUser: String): Single<Task> =
        apiServer.addLinkTask(idTask, link, idUser)

    override fun queryUserInTask(idTask: String): Single<Task> =
        apiServer.queryUserInTask(idTask)

    override fun changeDeadline(id: String, deadline: Long, idUser: String): Single<Task> =
        apiServer.changeDeadline(id, deadline, idUser)

    override fun queryAllSubtask(): Single<Subtask> = apiServer.queryAllSubtask()

    override fun queryAllHistory(): Single<History> = apiServer.queryAllHistory()

    override fun queryHistoryByUser(category: String, idUser: String): Single<History> =
        apiServer.queryHistoryByUser(category, idUser)

    override fun queryHistoryByRoom(category: String, idRoom: String): Single<History> =
        apiServer.queryHistoryByRoom(category, idRoom)

}