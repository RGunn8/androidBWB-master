package com.learning.leap.bwb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.learning.leap.bwb.helper.ScheduleBucket;

/**
 * Created by ryanlgunn8 on 9/6/17.
 */

public class AlarmReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduleBucket scheduleBucket = new ScheduleBucket(context);
        scheduleBucket.diviedTheBucketIntoThree();
    }
}
