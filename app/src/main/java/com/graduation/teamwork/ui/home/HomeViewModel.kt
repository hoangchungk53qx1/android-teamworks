package com.graduation.teamwork.ui.home

import android.util.Log
import com.bumptech.glide.Glide
import com.graduation.teamwork.domain.repository.room.RoomRepository
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * com.graduation.teamwork.ui.home
 * Created on 11/16/20
 */

class HomeViewModel(
    private val roomRepository: RoomRepository
) : BaseViewModel() {

    private val _resources =
        SingleLiveEvent<Resource<List<Map<TaskListItem.Header, List<TaskListItem.DtTask>>>>>()
    val resources: SingleLiveEvent<Resource<List<Map<TaskListItem.Header, List<TaskListItem.DtTask>>>>>
        get() = _resources

    private val TAG = "__HomeViewModel"

    fun getTaskWithRoom(idUser: String) {
        launch {
            roomRepository.getRoomByUser(idUser)
                .subscribeOn(Schedulers.io())
                .map { it ->
                    val result =
                        mutableListOf<Map<TaskListItem.Header, List<TaskListItem.DtTask>>>()

                    it.data?.let { rooms ->
                        for (room in rooms) {
                            val maps =
                                mutableMapOf<TaskListItem.Header, List<TaskListItem.DtTask>>()
                            val header = room.asHeader()

                            Log.d(TAG, "getTaskWithRoom: HEADER = $header")

                            if (!room.users.isNullOrEmpty() && !room.stages.isNullOrEmpty()) {
                                for (stage in room.stages) {
                                    stage?.tasks?.let { listTask ->

                                        if (maps[header].isNullOrEmpty()) {
                                            maps[header] = listTask
                                        } else {
                                            val tmpList = mutableListOf<TaskListItem.DtTask>()
                                            tmpList.addAll(maps[header]!!)
                                            tmpList.addAll(listTask)

                                            maps[header] = tmpList.distinctBy { it._id }
                                        }

                                    }
                                }
                            }

                            if (maps.isNotEmpty()) {
                                result.add(maps)
                            }
                        }
                    }

                    result
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null || data == null) {
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }
                        _resources.value = Resource.success(data)
                    }
                }


        }
    }


}