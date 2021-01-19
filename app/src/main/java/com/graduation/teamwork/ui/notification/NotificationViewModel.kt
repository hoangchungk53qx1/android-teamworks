package com.graduation.teamwork.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.graduation.teamwork.ui.base.ViewModelEvent
import com.graduation.teamwork.ui.base.ViewModelState
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent

/**
 * com.graduation.teamwork.ui.notification
 * Created on 11/16/20
 */

class NotificationViewModel : BaseViewModel() {

    private val _events = SingleLiveEvent<ViewModelEvent>()
    val events: SingleLiveEvent<ViewModelEvent>
        get() = _events

    private val _states = MutableLiveData<ViewModelState>()
    val states: LiveData<ViewModelState>
        get() = _states


}