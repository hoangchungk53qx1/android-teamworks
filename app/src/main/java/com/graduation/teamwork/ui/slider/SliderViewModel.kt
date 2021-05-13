package com.graduation.teamwork.ui.slider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.graduation.teamwork.domain.repository.group.GroupRepository
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.ui.base.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class SliderViewModel(private val groupRepository: GroupRepository) : BaseViewModel() {

    private val _resources = MutableLiveData<Resource<List<DtGroup>>>()
    val resources: LiveData<Resource<List<DtGroup>>>
        get() = _resources

    fun getAllGroupByUser(idUser: String) {
        launch {
            groupRepository.getAllGroupByUser(idUser)
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

}