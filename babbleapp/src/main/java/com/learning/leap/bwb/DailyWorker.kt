package com.learning.leap.bwb

import android.content.Context
import androidx.work.*
import com.learning.leap.bwb.tipReminder.TipReminder
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.Utility
import java.util.*
import java.util.concurrent.TimeUnit


class DailyWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        setReminders()

        return Result.success()
    }

    private fun setReminders() {
        // Set the alarm here.
        val tipPerReminder = Utility.readIntSharedPreferences(Constant.TIPS_PER_DAY, context)
        if (Utility.readBoolSharedPreferences(Constant.TIP_ONE_ON, context)) {
            val tipReminder = TipReminder(1, tipPerReminder, context)
            tipReminder.setAlarmForTip(
                Utility.readIntSharedPreferences(
                    Constant.START_TIME,
                    context
                )
            )
        }

        if (Utility.readBoolSharedPreferences(Constant.TIP_TWO_ON, context)) {
            val tipReminder = TipReminder(2, tipPerReminder, context)
            tipReminder.setAlarmForTip(Utility.readIntSharedPreferences(Constant.END_TIME, context))
        }
    }
}