package com.graduation.teamwork.ui.add

import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.domain.repository.RemoteRepository
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.ui.base.Error
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.ui.base.Success
import com.graduation.teamwork.ui.base.ViewModelEvent
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class AddViewModel(
        private val remoteRepository: RemoteRepository
) : BaseViewModel() {
    private val _resources =
            SingleLiveEvent<Resource<List<DtGroup>>>()
    val resources: SingleLiveEvent<Resource<List<DtGroup>>>
        get() = _resources

    private val _events = SingleLiveEvent<ViewModelEvent>()
    val events: SingleLiveEvent<ViewModelEvent>
        get() = _events

    fun getAllGroupForUser(idUser: String) {
        launch {
            remoteRepository.getAllGroupForUser(idUser)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { data, error ->
                        with(this) {
                            if (error != null) {
                                _resources.value = Resource.error(error.localizedMessage.orEmpty(), null)
                                return@with
                            }
                            _resources.value = Resource.success(data.data)

                        }
                    }
        }
    }

    fun addGroupInRoom(idGroup: String, nameRoom: String, idUser: String) {
        launch {
            remoteRepository.addRoomInGroup(idGroup, nameRoom, idUser)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { data, error ->
                        with(this) {
                            if (error != null) {
                                _events.value = Error(Constant.ErrorTag.ROOM.value, error)
                                return@with
                            }

                            _events.value = Success(data.data.last().toJson())
                        }
                    }
        }
    }
}