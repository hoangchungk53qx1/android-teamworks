package com.graduation.teamwork.di

import com.graduation.teamwork.ui.add.AddViewModel
import com.graduation.teamwork.ui.home.HomeViewModel
import com.graduation.teamwork.ui.login.LoginViewModel
import com.graduation.teamwork.ui.main.MainViewModel
import com.graduation.teamwork.ui.profile.ProfileViewModel
import com.graduation.teamwork.ui.room.RoomViewModel
import com.graduation.teamwork.ui.slider.SliderViewModel
import com.graduation.teamwork.ui.room.details.RoomDetailViewModel
import com.graduation.teamwork.ui.task.details.DetailViewModel
import com.graduation.teamwork.utils.AppHelper
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.FirebaseManager
import com.graduation.teamwork.utils.PrefsManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.dsl.module

/**
 * com.graduation.teamwork.di
 * Created on 11/10/20
 */

@OptIn(KoinApiExtension::class)
@JvmField
val appModule = module {

    // viewmodel
    viewModel { RoomViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { SliderViewModel(get()) }
    viewModel { AddViewModel(get()) }
    viewModel { RoomDetailViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { MainViewModel(get()) }

    // singleton
    single { PrefsManager() }
    single { DialogManager() }
    single { FirebaseManager() }
    single { AppHelper() }

}

// Gather all app modules
val onlineApp = listOf(appModule, remoteDataSourceModule)
val offlineApp = listOf(appModule, localAndroidDataSourceModule)
val roomApp = listOf(appModule, localAndroidDataSourceModule, roomDataSourceModule)