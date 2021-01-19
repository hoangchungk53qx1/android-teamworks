package com.graduation.teamwork.utils.schedulers

import android.os.Parcel
import android.os.Parcelable
import io.reactivex.Scheduler

/**
 * Rx Scheduler Provider
 */
interface BaseSchedulerProvider {

    fun io(): Scheduler
    fun ui(): Scheduler
    fun computation(): Scheduler
}