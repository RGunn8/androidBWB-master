package com.learning.leap.bwb.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.learning.leap.bwb.AlarmReciver;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ScheduleBucket {
    private Context context;
    private int firstVoteTips;
    private int secondVoteTips;
    private int thirdVoteTips;

    public ScheduleBucket(Context context) {
        this.context = context;
    }

    public static PendingIntent getAlarmPendingIntent(Context context) {
        Intent intent = new Intent(context, AlarmReciver.class);
        return PendingIntent.getBroadcast(context,10,intent,PendingIntent.FLAG_IMMUTABLE);
    }

    public static void setTipsNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.DATE, 1);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, getAlarmPendingIntent(context));
    }


    public void diviedTheBucketIntoThree() {
        int indexForUserStartTime = Utility.readIntSharedPreferences(Constant.START_TIME, context);
        int indexForUserEndTime = Utility.readIntSharedPreferences(Constant.ALL_TIME_END_TIME, context);
        if (indexForUserEndTime == 0) {
            indexForUserEndTime = getAllTimeIndex();
            Utility.writeIntSharedPreferences(Constant.ALL_TIME_END_TIME, indexForUserEndTime, context);

        }
        setUpBucket(indexForUserStartTime, indexForUserEndTime);

    }

    private int getAllTimeIndex() {
        int endTime = Utility.readIntSharedPreferences(Constant.END_TIME, context);
        String[] allEndTimes = context.getResources().getStringArray(R.array.end_times_tips_settings_array);
        String currentEndTime = allEndTimes[endTime];
        ArrayList<String> allTimes = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.all_times_settings_array)));
        return allTimes.indexOf(currentEndTime);
    }

    public void scheduleForFirstTime() {
        setUpBucket(8, 22);
    }

    private void setUpBucket(int startIndex, int endIndex) {
        removePlayToday();
        getNumberOfTipsPerNotification();

    }

    private String getMedian(List<String> times) {
        int lastItem = times.size();
        int median = lastItem / 2;
        return times.get(median);
    }

    private void removePlayToday() {
        // RealmResults<Notification> realmResults = realm.where(Notification.class).equalTo("mPlayToday", true).findAll();
//        for (Notification notification : realmResults) {
//            realm.beginTransaction();
//            notification.setPlayToday(false);
//            realm.copyToRealmOrUpdate(notification);
//            realm.commitTransaction();
//        }
    }


    private void getNumberOfTipsPerNotification() {
        int numberOfTotalTips;
        numberOfTotalTips = Utility.readIntSharedPreferences(Constant.NUM_OF_TIPS, context);

        switch (numberOfTotalTips) {
            case 3:
                setTipsForBuckets(1, 1, 1);
                break;
            case 4:
                setTipsForBuckets(1, 1, 2);
                break;
            case 5:
                setTipsForBuckets(1, 2, 2);
                break;
            case 6:
                setTipsForBuckets(2, 2, 2);
                break;
            case 7:
                setTipsForBuckets(2, 2, 3);
                break;
            case 8:
                setTipsForBuckets(2, 3, 3);
                break;
            case 9:
                setTipsForBuckets(3, 3, 3);
                break;
            default:
                setTipsForBuckets(3, 3, 4);

        }
    }

    private void setTipsForBuckets(int firstBucketTip, int secondBucketTip, int thridBucketTips) {
        firstVoteTips = firstBucketTip;
        secondVoteTips = secondBucketTip;
        thirdVoteTips = thridBucketTips;
    }

    private void getThreeBuckets(String[] times) {
        int lenght = times.length;
        String[] firstBucket = Arrays.copyOfRange(times, 0, (lenght / 3));
        String[] secondBucket = Arrays.copyOfRange(times, (lenght / 3), (2 * (lenght / 3)));
        String[] thirdBucket = Arrays.copyOfRange(times, (2 * (lenght / 3)), lenght);
        startBucket(firstBucket, 1, firstVoteTips);
        startBucket(secondBucket, 2, secondVoteTips);
        startBucket(thirdBucket, 3, thirdVoteTips);

    }

    private void startBucket(String[] bucket, int bucketNumber, int numberOfTips) {
        String firstTime = bucket[0];
        String endTime = bucket[bucket.length - 1];
        Date firstDateForTheBucket = updateConvertDateToToday(convertStringToDate(firstTime));
        Date endDateForTheBucket = updateConvertDateToToday(convertStringToDate(endTime));
        if (endDateForTheBucket != null && firstDateForTheBucket != null) {
//            TipReminder tipReminder = new TipReminder(bucketNumber, numberOfTips, firstDateForTheBucket, endDateForTheBucket, context);
//            tipReminder.setNotificationForBucket();
        }
    }

    private Date convertStringToDate(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date date;
        try {
            date = simpleDateFormat.parse(time);
            return date;

        } catch (ParseException ex) {
            return null;
        }
    }

    private Date updateConvertDateToToday(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar updatedCal = Calendar.getInstance();
            updatedCal.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            updatedCal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            updatedCal.set(Calendar.SECOND, 0);
            return updatedCal.getTime();
        } else {
            return null;
        }
    }


}
