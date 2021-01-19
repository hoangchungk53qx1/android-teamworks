package com.graduation.teamwork.di

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * com.graduation.teamwork.di
 * Created on 11/17/20
 */

val roomDataSourceModule = module {

    // Weather Room Data Repository
//    single<DailyForecastRepository>(override = true) {
//        DailyForecastRepositoryRoomImpl(
//            get(),
//            get()
//        )
//    }

    // Room Database
//    single {
//        Room.databaseBuilder(androidApplication(), WeatherDatabase::class.java, "weather-db")
//            .build()
//    }

    // Expose WeatherDAO
//    single { get<WeatherDatabase>().weatherDAO() }
//    single { get() }
}