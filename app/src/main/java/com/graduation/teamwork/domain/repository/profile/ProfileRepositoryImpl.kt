package com.graduation.teamwork.domain.repository.profile

import com.graduation.teamwork.data.remote.ApiServer
import com.graduation.teamwork.models.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody

class ProfileRepositoryImpl(private val apiServer: ApiServer) : ProfileRepository {
    override fun getInfoByName(username: String): Single<User> = apiServer.queryUserByName(username);
    override fun getInfoById(id: String): Single<User> = apiServer.queryUserById(id)
    override fun logout(id: String): Completable = apiServer.logout(id)
    override fun updateInfoById(id: String, numberphone: String, city: String, mail: String): Single<User> =  apiServer.updateUser(id,numberphone,city,mail)
    override fun updatePassword(id: String, oldPassword: String, newPassword: String): Single<User>  = apiServer.updatePasswored(id, oldPassword, newPassword)
    override fun updateAvatar(id: String, part: MultipartBody.Part): Single<User>  = apiServer.updateAvatar(id, part)

}