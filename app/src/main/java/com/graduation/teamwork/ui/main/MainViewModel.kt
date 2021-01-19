package com.graduation.teamwork.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.domain.repository.RemoteRepository
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.ui.base.Error
import com.graduation.teamwork.ui.base.Success
import com.graduation.teamwork.ui.base.ViewModelEvent
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * com.graduation.teamwork.ui.main
 * Created on 11/16/20
 */

class MainViewModel(
    private val remoteRepository: RemoteRepository
) : BaseViewModel() {

    private val TAG = "__MainViewModel"

    private val _resources = SingleLiveEvent<Resource<List<DtGroup>>>()
    val resources: SingleLiveEvent<Resource<List<DtGroup>>>
        get() = _resources

    private val _resourcesUser = SingleLiveEvent<Resource<List<DtUser>>>()
    val resourcesUser: SingleLiveEvent<Resource<List<DtUser>>>
        get() = _resourcesUser
    private val _resourcesTask = SingleLiveEvent<Resource<List<TaskListItem.DtTask>>>()
    val resourcesTask: SingleLiveEvent<Resource<List<TaskListItem.DtTask>>>
        get() = _resourcesTask
    private val _resourcesRoom = SingleLiveEvent<Resource<List<DtRoom>>>()
    val resourcesRoom: SingleLiveEvent<Resource<List<DtRoom>>>
        get() = _resourcesRoom

    private val _events = SingleLiveEvent<ViewModelEvent>()
    val events: LiveData<ViewModelEvent>
        get() = _events

    fun getAllGroup(idUser: String) {
        launch {
            remoteRepository.getAllGroupForUser(idUser)
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

    fun getAllMember() {
        launch {
            remoteRepository.getAllMember()
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

    fun getAllTask() {
        launch {
            remoteRepository.getAllTask()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            _resourcesTask.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _resourcesTask.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun getAllRoomByUser(idUser: String) {
        launch {
            remoteRepository.getAllRoomForUser(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            Log.d(TAG, "getAllRoomByUser: ROOM ERROR = ${error.localizedMessage}")
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }
                        Log.d(TAG, "getAllRoomByUser: ROOM SIZE = ${data.data?.size}")
                        _resourcesRoom.value = Resource.success(data.data)
                    }
                }
        }
    }


    fun addGroup(idUser: String, name: String) {
        launch {
            remoteRepository.addGroup(idUser, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            _events.value = Error(Constant.ERROR_TAG.GROUP.value, error)
//                            _resources.value =
//                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        _events.value = Success()

//                        _resources.value = Resource.success(data.data)
                    }
                }
        }
    }
}