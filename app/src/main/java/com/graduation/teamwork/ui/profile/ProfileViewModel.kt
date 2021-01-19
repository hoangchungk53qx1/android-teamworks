package com.graduation.teamwork.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.graduation.teamwork.domain.repository.profile.ProfileRepository
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.User
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.ui.base.Success
import com.graduation.teamwork.ui.base.ViewModelEvent
import com.graduation.teamwork.ui.base.ViewModelState
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import okhttp3.MultipartBody

class ProfileViewModel(private val profileRepository: ProfileRepository) : BaseViewModel() {
//    private val _resources = MutableLiveData<Resource<List<DtUser>>>()
//    val resources: LiveData<Resource<List<DtUser>>>
//        get() = _resources

    private val _resourcesUpdate = MutableLiveData<Resource<List<DtUser>>>()
    val resourceUpdate: LiveData<Resource<List<DtUser>>>
        get() = _resourcesUpdate


    private val _resources = SingleLiveEvent<Resource<List<DtUser>>>()
    val resources: SingleLiveEvent<Resource<List<DtUser>>>
        get() = _resources

    private val _resourceUpdatePassword = SingleLiveEvent<Resource<User>>()
    val resourceUpdatePassword: SingleLiveEvent<Resource<User>>
        get() = _resourceUpdatePassword

    private val _resourceUpdateAvatar = SingleLiveEvent<Resource<List<DtUser>>>()
    val resourceUpdateAvatar: SingleLiveEvent<Resource<List<DtUser>>>
        get() = _resourceUpdateAvatar

    private val _events = SingleLiveEvent<ViewModelEvent>()
    val events: LiveData<ViewModelEvent>
        get() = _events

    private val _states = MutableLiveData<ViewModelState>()
    val states: LiveData<ViewModelState>
        get() = _states

    fun getInfoById(id: String) {
        launch {
            profileRepository.getInfoById(id = id)
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

    fun updateInfoUser(id: String, mail: String, phone: String, city: String) {
        launch {
            profileRepository.updateInfoById(id = id, numberphone = phone, city = city, mail = mail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { update, error ->
                    with(this) {
                        if (error != null) {
                            _resourcesUpdate.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }
                        _resourcesUpdate.value = Resource.success(update.data)
                        _events.value = Success()

                    }
                }

        }
    }

    fun updatePassword(id: String, oldPassword: String, newPassword: String) {
        launch {
            profileRepository.updatePassword(
                id = id,
                oldPassword = oldPassword,
                newPassword = newPassword
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { update, error ->
                    with(this) {
                        if (error != null) {
                            _resourceUpdatePassword.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with

                        }
                        _resourceUpdatePassword.value = Resource.success(update)
                        _events.value = Success()
                    }
                }
        }
    }

    fun updateAvatar(id: String, part: MultipartBody.Part) {
        launch {
            profileRepository.updateAvatar(id = id, part = part)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { update, error ->
                    with(this) {
                        if (error != null) {
                            Log.d(TAG, "updateAvatar: ERROR ${error.localizedMessage}")
                            _resourceUpdateAvatar.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }
                        _resourceUpdateAvatar.value = Resource.success(update.data)
                        _events.value = Success()
                    }
                }
        }
    }
    private val TAG = "__ProfileViewModel"

    fun logout(id: String) {
        launch {
            profileRepository.logout(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    _events.value = Success()
                }
        }
    }
}