package com.graduation.teamwork.utils.schedulers

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers


class SchedulerProvider {
    fun io(): Scheduler = Schedulers.io()

    fun ui() = AndroidSchedulers.mainThread()!!

    fun computation(): Scheduler = Schedulers.computation()
}