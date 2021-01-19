package com.graduation.teamwork.ui.slider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.graduation.teamwork.domain.repository.group.GroupRepository
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class SliderViewModel(private val groupRepository: GroupRepository) : BaseViewModel() {

    private val _resources = MutableLiveData<Resource<List<DtGroup>>>()
    val resources: LiveData<Resource<List<DtGroup>>>
        get() = _resources

    private val TAG = "__SliderViewModel"

    fun getAllGroupByUser(idUser: String) {
        launch {
            Log.d(TAG, "getAllGroupByUser: ${idUser}")
            groupRepository.getAllGroupByUser(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            Log.d(TAG, "getAllGroupByUser: ${error.localizedMessage}")
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }

                        Log.d(TAG, "getAllGroupByUser: ${data.data.size}")
                        _resources.value = Resource.success(data.data)

                    }
                }
        }
    }

}