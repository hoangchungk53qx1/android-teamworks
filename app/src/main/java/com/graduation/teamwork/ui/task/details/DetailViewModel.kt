package com.graduation.teamwork.ui.task.details

import android.util.Log
import com.graduation.teamwork.domain.repository.RemoteRepository
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.UserInRoom
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class DetailViewModel(
    private val remoteRepository: RemoteRepository
) : BaseViewModel() {

    private val _resources = SingleLiveEvent<Resource<TaskListItem.DtTask>>()
    val resources: SingleLiveEvent<Resource<TaskListItem.DtTask>>
        get() = _resources
    private val _resourcesUpdate = SingleLiveEvent<Resource<List<TaskListItem.DtTask>>>()
    val resourcesUpdate: SingleLiveEvent<Resource<List<TaskListItem.DtTask>>>
        get() = _resourcesUpdate

    private val _resourcesRoom = SingleLiveEvent<Resource<List<DtRoom>>>()
    val resourcesRoom: SingleLiveEvent<Resource<List<DtRoom>>>
        get() = _resourcesRoom

    private val _resourcesUser = SingleLiveEvent<Resource<List<UserInRoom>>>()
    val resourcesUser: SingleLiveEvent<Resource<List<UserInRoom>>>
        get() = _resourcesUser

    fun addUserInTask(idTask: String,
                      idUser: String,
                      idUserAction: String){
        launch {
            remoteRepository.addMemberInTask(idTask, idUser, idUserAction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            _resourcesUpdate.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resourcesUpdate.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun changeDeadline(id: String, deadline: Long, idUser: String){
        launch {
            remoteRepository.changeDeadline(id, deadline, idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            _resourcesUpdate.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resourcesUpdate.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun queryMemberInTask(idTask: String){
        launch {
            remoteRepository.queryMemberInTask(idTask)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            _resourcesUpdate.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resourcesUpdate.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun addSubtask(idTask: String, nameSubtask: String, idUser: String) {
        launch {
            remoteRepository.addSubtaskInTask(idTask, nameSubtask, idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resources.value = Resource.success(data.data?.first())
                    }
                }
        }
    }

    fun addUserInRoom(
        idRoom: String,
        idUserAction: String,
        idUserAdded: String
    ) {
        launch {
            remoteRepository.addUserInRoom(idRoom, idUserAction, idUserAdded)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->

                    with(this) {
                        if (error != null) {
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resourcesRoom.value = Resource.success(data.data)
                    }

                }
        }
    }

    fun deleteUserInRoom(
        idRoom: String,
        idUser: String,
        idUserWillDeleted: String
    ) {
        launch {
            remoteRepository.deleteUserInRoom(idRoom, idUser, idUserWillDeleted)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->

                    with(this) {
                        if (error != null) {
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resourcesRoom.value = Resource.success(data.data)
                    }

                }
        }
    }

    fun getAllUserInRoom(idRoom: String) {
        launch {
            remoteRepository.getAllMemberInRoom(idRoom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->

                    with(this) {
                        if (error != null) {
                            _resourcesUser.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resourcesUser.value = Resource.success(data.data)
                    }

                }
        }
    }

    fun setLevel(idRoom: String, idUser: String, idUserWillSet: String, level: Long) {
        launch {
            remoteRepository.setLevel(idRoom, idUser, idUserWillSet, level.toInt())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->

                    with(this) {
                        if (error != null) {
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resourcesRoom.value = Resource.success(data.data)
                    }

                }
        }
    }

}