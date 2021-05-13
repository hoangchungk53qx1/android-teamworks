package com.graduation.teamwork.domain.repository.profile

import com.graduation.teamwork.models.Room
import com.graduation.teamwork.models.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody

interface ProfileRepository {
    fun getInfoByName(username: String): Single<User>
    fun getInfoById(id: String): Single<User>
    fun logout(id: String): Completable
    fun updateInfoById(id: String,numberPhone: String,city: String,mail: String):Single<User>
    fun updatePassword(id:String,oldPassword: String ,newPassword: String):Single<User>
    fun updateAvatar(id:String,part: MultipartBody.Part): Single<User>

}