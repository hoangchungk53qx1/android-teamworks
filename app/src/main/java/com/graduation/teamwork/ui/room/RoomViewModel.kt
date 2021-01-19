package com.graduation.teamwork.ui.room

import android.util.Log
import com.graduation.teamwork.domain.repository.room.RoomRepository
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * com.graduation.teamwork.ui.room
 * Created on 11/16/20
 */

class RoomViewModel(val roomRepository: RoomRepository) : BaseViewModel() {

    private val TAG = "__RoomViewModel"

    private val _resources = SingleLiveEvent<Resource<List<DtRoom>>>()
    val resources: SingleLiveEvent<Resource<List<DtRoom>>>
        get() = _resources

    fun getAllRoomInGroup(idGroup: String) {
        Log.d(TAG, "getAllRoomInGroup: ${idGroup}")
        launch {
            roomRepository.getAllRoomByGroup(idGroup)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            Log.d(TAG, "getAllRoomInGroup: ${error.localizedMessage}")
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        Log.d(TAG, "getAllRoomInGroup: ${data.data?.size}")

                        _resources.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun getRoomByUser(id: String) {
        launch {
            Log.d(TAG, "getRoomByUser: ${id}")
            roomRepository.getRoomByUser(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            Log.d(TAG, "getRoomByUser: ${error.localizedMessage}")
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        Log.d(TAG, "getRoomByUser: ${data.data?.size}")
                        _resources.value = Resource.success(data.data)

                    }
                }
        }
    }

}