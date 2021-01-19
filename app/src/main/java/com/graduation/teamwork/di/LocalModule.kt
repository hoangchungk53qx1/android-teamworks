package com.graduation.teamwork.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Local Json Files Datasource
 */
val localAndroidDataSourceModule = module {
//    single<JsonReader> { AndroidJsonReader(androidApplication()) }
//    single<WeatherDataSource> { FileDataSource(get(), true) }
}