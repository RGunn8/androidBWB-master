package com.learning.leap.bwb.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.learning.leap.bwb.room.BabbleDatabase


class ResetPlayTodayWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = BabbleDatabase.getInstance(applicationContext)
            val playTodayTips = db.babbleTipDAO().getTipsForPlayToday()
            for (tip in playTodayTips) {
                tip.playToday = false
                db.babbleTipDAO().updateTip(tip)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}