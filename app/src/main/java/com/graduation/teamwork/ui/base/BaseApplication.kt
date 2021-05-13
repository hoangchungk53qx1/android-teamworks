package com.graduation.teamwork.ui.base

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.multidex.MultiDex
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.di.offlineApp
import com.graduation.teamwork.di.onlineApp
import com.pixplicity.easyprefs.library.Prefs
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


/**
 * com.graduation.teamwork.ui.base
 * Created on 11/8/20
 */

class BaseApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		MultiDex.install(this)
		
		startKoin {
			androidLogger(Level.DEBUG)
			androidContext(this@BaseApplication)
			// module
			modules(onlineApp)

		}

		Prefs.Builder()
			.setContext(this)
			.setMode(ContextWrapper.MODE_PRIVATE)
			.setPrefsName(packageName)
			.setUseDefaultSharedPreference(true)
			.build()

		createNotificationChannel()
	}

	private fun createNotificationChannel() {
		// Make a channel if necessary
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Create the NotificationChannel, but only on API 26+ because
			// the NotificationChannel class is new and not in the support library
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel = NotificationChannel(
				Constant.NotifyKey.CHANEL_ID,
				Constant.NotifyKey.CHANEL_NAME,
				importance
			).apply {
				description = Constant.NotifyKey.CHANEL_DESCRIPTION
				lightColor = Color.BLUE
				enableLights(true)
				enableVibration(false)
			}

			// Add the channel
//			val notificationManager =
//					(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
//			notificationManager.createNotificationChannel(channel)
			(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
				createNotificationChannel(channel)
			}
		}
	}

}