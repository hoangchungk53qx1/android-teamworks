package com.graduation.teamwork.ui.base

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data = data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data = data, message = msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data = data, message = null)
        }
    }
}