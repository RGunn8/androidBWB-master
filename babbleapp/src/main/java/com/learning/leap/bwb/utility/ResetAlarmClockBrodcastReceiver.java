package com.learning.leap.bwb.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.learning.leap.bwb.PlayTodayJob;
import com.learning.leap.bwb.tipReminder.TipReminder;

/**
 * Created by ryangunn on 10/10/16.
 */

public class ResetAlarmClockBrodcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            int tipPerReminder = Utility.readIntSharedPreferences(Constant.TIPS_PER_DAY, context);
            if (Utility.readBoolSharedPreferences(Constant.TIP_ONE_ON, context)) {
                TipReminder tipReminder = new TipReminder(1, tipPerReminder, context);
                tipReminder.setAlarmForTip(Utility.readIntSharedPreferences(Constant.START_TIME, context));
            }

            if (Utility.readBoolSharedPreferences(Constant.TIP_TWO_ON, context)) {
                TipReminder tipReminder = new TipReminder(2, tipPerReminder, context);
                tipReminder.setAlarmForTip(Utility.readIntSharedPreferences(Constant.END_TIME, context));
            }

        }
    }
}
