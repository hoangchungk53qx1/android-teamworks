package com.graduation.teamwork.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel

import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * ViewModel for Rx Jobs
 *
 * launch() - launch a Rx request
 * clear all request on stop
 */
abstract class BaseViewModel : ViewModel() {

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