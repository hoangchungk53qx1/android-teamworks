package com.graduation.teamwork.di

import com.graduation.teamwork.data.local.Configs
import com.graduation.teamwork.data.remote.ApiServer
import com.graduation.teamwork.domain.repository.RemoteRepository
import com.graduation.teamwork.domain.repository.RemoteRepositoryImpl
import com.graduation.teamwork.domain.repository.group.GroupRepository
import com.graduation.teamwork.domain.repository.group.GroupRepositoryImpl
import com.graduation.teamwork.domain.repository.profile.ProfileRepository
import com.graduation.teamwork.domain.repository.profile.ProfileRepositoryImpl
import com.graduation.teamwork.domain.repository.room.RoomRepository
import com.graduation.teamwork.domain.repository.room.RoomRepositoryImpl
import com.graduation.teamwork.domain.repository.task.TaskRepository
import com.graduation.teamwork.domain.repository.task.TaskRepositoryImpl
import com.graduation.teamwork.domain.resposity.login.LoginRepository
import com.graduation.teamwork.domain.repository.login.LoginRepositoryImpl
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * com.graduation.teamwork.di
 * Created on 11/16/20
 */

val remoteDataSourceModule = module {
    // provided web components
    single { createOkHttpClient() }
    // Fill property
    single { createWebService<ApiServer>(get(), Configs.Network.BASE_URL.value) }
    single<RoomRepository> { RoomRepositoryImpl(get()) }
    single<LoginRepository> { LoginRepositoryImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<GroupRepository> { GroupRepositoryImpl(get()) }
    single<RemoteRepository> { RemoteRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }

}

object Properties {
    val SERVER_URL = Configs.Network.BASE_URL.value
}

fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

    val TIME_OUT = 60L

    return OkHttpClient.Builder()
        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor).build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create()).build()
    return retrofit.create(T::class.java)
}