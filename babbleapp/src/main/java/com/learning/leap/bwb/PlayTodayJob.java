package com.learning.leap.bwb;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobRequest;
import com.learning.leap.bwb.helper.ScheduleBucket;

import java.util.concurrent.TimeUnit;


public class PlayTodayJob extends DailyJob {

    public static final String PLAY_TODAY = "PLAY_TODAY";


    public static void schedule() {
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.MINUTE, 1);
        JobRequest.Builder builder = new JobRequest.Builder(PLAY_TODAY)
                .setUpdateCurrent(false);
        DailyJob.schedule(builder, TimeUnit.HOURS.toMillis(1), TimeUnit.HOURS.toMillis(2));
    }


    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(Params params) {
        if (Build.VERSION.SDK_INT != 26) {
            Intent scheduleBucketIntentServiceIntent = new Intent(getContext(), ScheduleBucketIntentService.class);
            getContext().startService(scheduleBucketIntentServiceIntent);
        } else {
            ScheduleBucket scheduleBucket = new ScheduleBucket(getContext());
            scheduleBucket.diviedTheBucketIntoThree();
        }
        return DailyJobResult.SUCCESS;
    }
}
