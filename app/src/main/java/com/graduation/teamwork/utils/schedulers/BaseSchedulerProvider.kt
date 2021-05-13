package com.graduation.teamwork.utils.schedulers

import io.reactivex.rxjava3.core.Scheduler


/**
 * Rx Scheduler Provider
 */
interface BaseSchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
    fun computation(): Scheduler
}