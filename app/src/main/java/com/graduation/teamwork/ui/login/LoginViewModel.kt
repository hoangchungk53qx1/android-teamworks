package com.graduation.teamwork.ui.login

import com.graduation.teamwork.domain.resposity.login.LoginRepository
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
}

class LoginViewModel(private val loginRepository: LoginRepository) : BaseViewModel() {
    private val _resources = SingleLiveEvent<Resource<List<DtUser>>>()
    val resources: SingleLiveEvent<Resource<List<DtUser>>>
        get() = _resources

    fun loginWithMail(email: String, password: String) {
        launch {
            loginRepository.loginWithMail(email, password)
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

    fun addUser(fullName: String, password: String, mail: String) {
        launch {
            loginRepository.addUser(fullName, password, mail)
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