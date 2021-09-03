package com.learning.leap.bwb;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.learning.leap.bwb.helper.ScheduleBucket;
import com.learning.leap.bwb.tipReminder.TipReminder;

import java.util.Date;


public class ScheduleBucketIntentService extends IntentService {

    Context context;

    public ScheduleBucketIntentService() {
        super("ScheduleBucketIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ScheduleBucket scheduleBucket = new ScheduleBucket(context);
        scheduleBucket.diviedTheBucketIntoThree();
        Log.d("lit", "onReceive called: ");


    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
