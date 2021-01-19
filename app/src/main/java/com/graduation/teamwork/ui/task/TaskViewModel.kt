package com.graduation.teamwork.ui.task

import android.util.Log
import androidx.lifecycle.LiveData
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.domain.repository.task.TaskRepository
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtStage
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.ui.base.Error
import com.graduation.teamwork.ui.base.Success
import com.graduation.teamwork.ui.base.ViewModelEvent
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class TaskViewModel(
    private val taskRepository: TaskRepository
) : BaseViewModel() {

    private val TAG = "__TaskViewModel"

    private val _events = SingleLiveEvent<ViewModelEvent>()
    val events: LiveData<ViewModelEvent>
        get() = _events

    private val _resources = SingleLiveEvent<Resource<List<DtStage>>>()
    val resources: SingleLiveEvent<Resource<List<DtStage>>>
        get() = _resources

    private val _resourcesUser = SingleLiveEvent<Resource<List<DtUser>>>()
    val resourcesUser: SingleLiveEvent<Resource<List<DtUser>>>
        get() = _resourcesUser

    private val _resourcesRoom = SingleLiveEvent<Resource<List<DtRoom>>>()
    val resourcesRoom: SingleLiveEvent<Resource<List<DtRoom>>>
        get() = _resourcesRoom

    fun addTaskInStage(idStage: String, nameTask: String) {
        launch {
            taskRepository.addTaskInStage(idStage, nameTask)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resources.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun addStageInRoom(idRoom: String, nameStage: String, idUser: String) {
        launch {
            taskRepository.addStageInRoom(idRoom, nameStage, idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        Log.d(TAG, "addStageInRoom: ${data.data}")

                        _resourcesRoom.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun getAllUser(id: String) {
        launch {
            taskRepository.getAllUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->

                    with(this) {
                        if (error != null) {
                            _resourcesUser.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        Log.d(TAG, "addStageInRoom: ${data.data}")

                        _resourcesUser.value = Resource.success(data.data)
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
            taskRepository.addUserInRoom(idRoom, idUserAction, idUserAdded)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->

                    with(this) {
                        if (error != null) {
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        Log.d(TAG, "addStageInRoom: ${data.data}")

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
            taskRepository.deleteUserInRoom(idRoom, idUser, idUserWillDeleted)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->

//                    with(this) {
//                        if (error != null) {
//                            _events.value = Error(Constant.ERROR_TAG.TASK.value, error)
//                            return@with
//                        }
//
//                        Log.d(TAG, "addStageInRoom: ${data.data}")
//
//                        _events.value = Success()
//                    }

                    with(this) {
                        if (error != null) {
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        Log.d(TAG, "addStageInRoom: ${data.data}")

                        _resourcesRoom.value = Resource.success(data.data)
                    }

                }
        }
    }
}