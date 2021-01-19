package com.graduation.teamwork.utils.eventbus

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

object RxBus {
    private val publisher = PublishSubject.create<Any>()
    private val behavior = BehaviorSubject.create<Any>()

    /**
     * Emit item to listenPublisher by publish subject.
     */
    fun publishToPublishSubject(event: Any) {
        publisher.onNext(event)
    }

    /**
     * Emit item to listenPublisher by behavior subject.
     */
    fun publishToBehaviorSubject(event: Any) {
        behavior.onNext(event)
    }

    /**
     * Listen should return an Observable and not the publisher
     * Using ofType we filter only events that match that class type
     */
    fun <T> listenPublisher(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)

    /**
     * Listen should return an Observable and not the behavior
     * Using ofType we filter only events that match that class type
     */
    fun <T> listenBehavior(eventType: Class<T>): Observable<T> = behavior.ofType(eventType)
}
