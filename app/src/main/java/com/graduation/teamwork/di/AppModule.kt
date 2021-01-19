package com.graduation.teamwork.di

import com.graduation.teamwork.ui.add.AddViewModel
import com.graduation.teamwork.ui.home.HomeViewModel
import com.graduation.teamwork.ui.login.LoginViewModel
import com.graduation.teamwork.ui.main.MainViewModel
import com.graduation.teamwork.ui.profile.ProfileViewModel
import com.graduation.teamwork.ui.room.RoomViewModel
import com.graduation.teamwork.ui.slider.SliderViewModel
import com.graduation.teamwork.ui.task.TaskViewModel
import com.graduation.teamwork.ui.task.details.DetailViewModel
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.FirebaseManager
import com.graduation.teamwork.utils.PrefsManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * com.graduation.teamwork.di
 * Created on 11/10/20
 */

@JvmField
val appModule = module {

//    // scope for RoomFragment
//    scope(named<RoomFragment>()) {
//        // scoped RoomViewModel instance
//        scoped { RoomViewModel(get()) }
//    }

    // viewmodel
    viewModel { RoomViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { SliderViewModel(get()) }
    viewModel { AddViewModel(get()) }
    viewModel { TaskViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { MainViewModel(get()) }

    // singleton
    single { PrefsManager() }
    single { DialogManager() }
    single { FirebaseManager() }

}

// Gather all app modules
val onlineApp = listOf(appModule, remoteDataSourceModule)
val offlineApp = listOf(appModule, localAndroidDataSourceModule)
val roomApp = listOf(appModule, localAndroidDataSourceModule, roomDataSourceModule)