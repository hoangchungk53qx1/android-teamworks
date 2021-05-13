package com.graduation.teamwork.domain.repository

import com.graduation.teamwork.models.*
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface RemoteRepository {
    fun getAllRoom(): Single<Room>
    fun getAllMember(): Single<User>
    fun getAllTask(): Single<Task>
    fun getAllRoomForUser(idUser: String): Single<Room>
    fun getAllGroupForUser(idUser: String): Single<Group>
    fun getAllMemberInRoom(idRoom: String): Single<BaseModel>
    fun getRoom(id: String): Single<Room>
    fun addRoomInGroup(idGroup: String, nameRoom: String, idUser: String): Single<Group>
    fun addSubtaskInTask(idTask: String, nameStage: String, idUser: String): Single<Task>
    fun addGroup(idUser: String, name: String): Single<Group>
    fun addUserInRoom(idRoom: String, idUserAction: String, idUserAdded: String): Single<Room>
    fun deleteUserInRoom(idRoom: String, idUser: String, idUserWillDeleted: String): Single<Room>


    fun setLevel(idRoom: String, idUser: String, idUserWillSet: String, level: Int): Single<Room>

    //task
    fun changeDeadline(id: String, deadline: Long, idUser: String): Single<Task>
    fun addMemberInTask(idTask: String, idUser: String, idUserAction: String): Single<Task>
    fun deleteMemberInTask(idTask: String, idUser: String, idUserAction: String): Single<Task>
    fun queryMemberInTask(idTask: String): Single<Task>
    fun updateLabel(id: String, labels: List<Int>): Single<Task>
    fun queryTaskWithId(id: String): Single<Task>
    fun uploadImageTask(id: String, part: MultipartBody.Part, idUser: String): Single<Task>
    fun uploadLink(id: String, link: String, idUser: String): Single<Task>

    //subtask
    fun setCompleted(id: String, name: String, isCompleted: Boolean): Single<Subtask>
}