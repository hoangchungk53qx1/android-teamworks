package com.graduation.teamwork.utils.notify

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import com.graduation.teamwork.data.local.Constant
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

/**
 * Created by
 * Date 07/03/2020.
 */
class NotifyHelper private constructor(context: Context) {
    private val mWorkManager: WorkManager = WorkManager.getInstance(context)
    private val workInfor: LiveData<List<WorkInfo>>
    fun schedule(timeDelay: Long) {
        val oneRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setInitialDelay(timeDelay, TimeUnit.MILLISECONDS)
            .build()
        mWorkManager
            .enqueue(oneRequest)
    }

    private fun calculateInitialDelay(): Long {
        val hour = 10
        val calendar = Calendar.getInstance()
        val currentTimeMillis = calendar.timeInMillis
        if (calendar[Calendar.HOUR_OF_DAY] >= hour && calendar[Calendar.MINUTE] > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        Log.d(TAG, "calculateInitialDelay: " + (calendar.timeInMillis - currentTimeMillis))
        return calendar.timeInMillis - currentTimeMillis
    }

    fun isWorkScheduled(tag: String?): Boolean {
        Log.d(TAG, "isWorkScheduled: ")
        val instance = WorkManager.getInstance()
        val statuses = instance.getWorkInfosByTag(
            tag!!
        )
        return try {
            var running = false
            val workInfoList = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = (state == WorkInfo.State.RUNNING) or (state == WorkInfo.State.ENQUEUED)
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }

    fun cancelWork() {
        Log.d(TAG, "cancelWork: ")
        mWorkManager.cancelAllWork()
    }

    companion object {
        var mInstance: NotifyHelper? = null
        fun getInstance(context: Context): NotifyHelper? {
            if (mInstance == null) {
                synchronized(NotifyHelper::class.java) { mInstance = NotifyHelper(context) }
            }
            return mInstance
        }

        private const val TAG = "111_NotifyHelper"
    }

    init {
        workInfor = mWorkManager.getWorkInfosByTagLiveData(Constant.NotifyKey.WORKER_NAME)
    }
}