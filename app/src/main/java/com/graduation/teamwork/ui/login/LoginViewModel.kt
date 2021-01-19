 package com.graduation.teamwork.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.graduation.teamwork.domain.resposity.login.LoginRepository
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
}

class LoginViewModel(private val loginRepository: LoginRepository) : BaseViewModel() {
    val loginSuccess = MutableLiveData<Boolean>()
    val loginFailedMessage = MutableLiveData<String?>()
    var email: String = "";
    var password: String = "";
    private val _resources = SingleLiveEvent<Resource<List<DtUser>>>()
    val resources: SingleLiveEvent<Resource<List<DtUser>>>
        get() = _resources

//    private val _events = SingleLiveEvent<ViewModelEvent>()
//    val events: LiveData<ViewModelEvent>
//        get() = _events
//
//    private val _states = MutableLiveData<ViewModelState>()
//    val states: LiveData<ViewModelState>
//        get() = _states

    fun loginWithMail(email: String, password: String) {
        launch {
            loginRepository.loginWithMail(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            Log.d(TAG, "loginWithMail: err ${error.localizedMessage}")
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@with
                        }
                        _resources.value = Resource.success(data.data)

                    }
                }
        }
    }

    private val TAG = "__LoginViewModel"

    fun addUser(fullname: String, password: String, mail: String) {
        launch {
            loginRepository.addUser(fullname, password, mail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    with(this) {
                        if (error != null) {
                            Log.d(TAG, "addUser: err = ${error.localizedMessage}")
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