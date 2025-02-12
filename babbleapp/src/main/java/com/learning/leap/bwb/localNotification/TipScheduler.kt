package com.learning.leap.bwb.localNotification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import javax.inject.Inject

class TipScheduler @Inject constructor(val context: Context) {

    companion object {
        const val NUM_OF_TIP = "NumberOfTips"
        const val TIP_ID = "TipId"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private fun createPendingIntent(tipNotification: TipNotification): PendingIntent {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.putExtra(TIP_ID, tipNotification.id)
        intent.putExtra(NUM_OF_TIP, tipNotification.numberOfTip)
        return PendingIntent.getBroadcast(
            context,
            tipNotification.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun schedule(tipNotification: TipNotification) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            tipNotification.time,
            AlarmManager.INTERVAL_DAY,
            createPendingIntent(tipNotification)
        )
    }

    fun testSchedule(tipNotification: TipNotification) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            tipNotification.time,
            6000,
            createPendingIntent(tipNotification)
        )
    }

    fun cancel(tipNotification: TipNotification) {
        alarmManager.cancel(
            createPendingIntent(tipNotification)
        )
    }
}