package com.graduation.teamwork.domain.repository.login

import com.graduation.teamwork.data.remote.ApiServer
import com.graduation.teamwork.domain.resposity.login.LoginRepository
import com.graduation.teamwork.models.User
import io.reactivex.rxjava3.core.Single

class LoginRepositoryImpl(private val apiServer: ApiServer) : LoginRepository {
    override fun loginWithMail(mail: String, password: String): Single<User> =
        apiServer.loginWithMail(mail, password);

    override fun addUser(fullname: String, password: String, mail: String): Single<User> =
        apiServer.addUser(fullname, password, mail)
}