package com.graduation.teamwork.utils.notify

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.ui.login.LoginActivity
import com.graduation.teamwork.ui.main.MainActivity
import java.util.*

/**
 * Created by TranTien
 * Date 07/03/2020.
 */
object NotifyUtils {
    private var mNotifies: MutableMap<String, String> = mutableMapOf()
    private val random = Random()
    fun initListNotify() {
        mNotifies = HashMap()
        mNotifies["New addons available! Top Rated Minecraft Adventure Maps"] =
            "This update contains an up-to-date list of the top 100 highest rated adventure maps on our app"
        mNotifies["Free minecraft premium account Today"] =
            "I will help you access the Minecraft Premium Addons. Claim your Minecraft-Premium Addons today! 100% free and legal"
        mNotifies["Best Minecraft Addons Updated Daily"] =
            "A list of the best Minecraft Addons determined by Us from Fox and updated on a daily basis. ... New Minecraft Add-Ons That You Should Try"
        mNotifies["Minecraft Add-Ons: Customize Your Experience"] =
            "Now available on mobile: Add-Ons are the first step on our journey towards bringing even greater levels of customisation to all editions"
        mNotifies["How to Install Minecraft Mods"] =
            "Installing Minecraft mods isn't rocket science, nor is it child's play. Here's our guide to installing mods for every system that Minecraft is available"
        mNotifies["New Minecraft Mod Teaches You Code as You Play"] =
            "Furniture Mod. Adds over 80 unique Furniture to Minecaft · Gun Mod. Adds in simple modular guns! Controllable. Adds controller support to Minecraft"
        mNotifies["Installing Minecraft Mods"] =
            "Assuming you've already installed Minecraft, let's look at how to install mods. But first, a word about safety. There are a lot of viruses out there, so be careful"
    }

    private const val TAG = "___AAAA___"

    @JvmStatic
    fun makeStatusNotification(context: Context) {
        val activityIntent = Intent(context, LoginActivity::class.java)
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val largeIcon: Bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.default_icon_app)

        // Create the notification
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, Constant.NOTIFY.CHANEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Thông báo")
                .setContentText("Có công việc gần đến hạn!!!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Có công việc gần đến hạn!!!")
                )

        // Show the notification
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManager.notify(Constant.NOTIFY.NOTIFICATION_ID, builder.build())
    }
}