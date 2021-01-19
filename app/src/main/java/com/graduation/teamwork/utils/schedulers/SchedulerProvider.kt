package com.graduation.teamwork.utils.schedulers

import com.graduation.teamwork.utils.schedulers.BaseSchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SchedulerProvider : BaseSchedulerProvider {
    override fun io() = Schedulers.io()

    override fun ui() = AndroidSchedulers.mainThread()

    override fun computation() = Schedulers.computation()
}