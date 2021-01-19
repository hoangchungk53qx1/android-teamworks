package com.graduation.teamwork.extensions

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * com.graduation.teamwork.extensions
 * Created on 11/8/20
 */

/**
 * Use this extension for Observable
 */
internal fun <T> Observable<T>.observeOnUiThread(): Observable<T> =
	this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

internal fun Completable.observeOnUiThread(): Completable =
	this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

internal fun <T> Single<T>.observerOnUiThread(): Single<T> =
	this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())