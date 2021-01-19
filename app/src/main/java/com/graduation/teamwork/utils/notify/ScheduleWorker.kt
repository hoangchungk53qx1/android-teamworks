package com.graduation.teamwork.utils.notify

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.graduation.teamwork.utils.notify.NotifyUtils.makeStatusNotification

/**
 * Created by TranTien
 * Date 07/02/2020.
 */
class ScheduleWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("___AAAA___", "doWork: ")
        makeStatusNotification(applicationContext)
        return Result.success()
    }
}