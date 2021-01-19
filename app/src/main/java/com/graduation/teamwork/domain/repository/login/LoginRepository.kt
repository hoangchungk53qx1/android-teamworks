package com.graduation.teamwork.domain.resposity.login

import com.graduation.teamwork.models.User
import io.reactivex.rxjava3.core.Single

interface LoginRepository {
    fun loginWithMail(mail: String, password: String): Single<User>
    fun addUser(fullname: String, password: String, mail: String): Single<User>
}