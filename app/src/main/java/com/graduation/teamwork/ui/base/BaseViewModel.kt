package com.graduation.teamwork.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import com.graduation.teamwork.utils.AppHelper
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.schedulers.SchedulerProvider
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * ViewModel for Rx Jobs
 *
 * launch() - launch a Rx request
 * clear all request on stop
 */
@KoinApiExtension
abstract class BaseViewModel : ViewModel(), KoinComponent {
    protected val appHelper: AppHelper by inject()
    protected val schedulerProvider = SchedulerProvider()
    protected val prefs: PrefsManager by inject()

    private val disposables = CompositeDisposable()


    fun launch(job: () -> @NonNull Disposable) {
        disposables.add(job())
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}