package com.graduation.teamwork.extensions

import com.graduation.teamwork.models.DtUser
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/**
 * com.graduation.teamwork.extensions
 * Created on 11/24/20
 */
val moshi = Moshi.Builder().build()

inline fun <reified T> List<T>.toJson(): String {
    val type = Types.newParameterizedType(List::class.java, T::class.java)
    val adapter = moshi.adapter<List<T>>(type)

    return adapter.toJson(this)
}

inline fun <reified T> String.fromJson(): List<T>? {
    val type = Types.newParameterizedType(List::class.java, T::class.java)
    val adapter = moshi.adapter<List<T>>(type)

    return adapter.fromJson(this)
}

//fun DtUser.toJson(): String {
//    val moshi = Moshi.Builder().build()
//    val jsonAdapter = moshi.adapter<DtUser>(
//        DtUser::class.java
//    )
//    return jsonAdapter.toJson(this)
//}

inline fun <reified T>T.toJson(): String {
    val jsonAdapter = moshi.adapter<T>(
        T::class.java
    )
    return jsonAdapter.toJson(this)
}

inline fun <reified T>String.fromObject(): T? {
    val jsonAdapter = moshi.adapter<T>(
        T::class.java
    )
    return jsonAdapter.fromJson(this)
}