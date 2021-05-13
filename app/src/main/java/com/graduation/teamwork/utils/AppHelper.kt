package com.graduation.teamwork.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class AppHelper {
    companion object {
        const val DAILY_STATS: Int = 1;
        const val YESTERDAY_STATS: Int = 2
        const val WEEKLY_STATS: Int = 3;
        const val MONTHLY_STATS: Int = 4;
        const val NETWORK_MODE = 5;
    }

    private var mUsageStatsManager: UsageStatsManager? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun initAppHelper(context: Context) {
        if (mUsageStatsManager == null) {
            mUsageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        }
    }

    fun getUsageStatsManager() = mUsageStatsManager

    fun getHours(millis: Long): Float {
        val seconds = (millis / 1000).toFloat()
        val minutes = seconds / 60
        return minutes / 60
    }

    fun getMinutes(millis: Long): Float {
        val seconds = (millis / 1000).toFloat()
        return seconds / 60
    }

}