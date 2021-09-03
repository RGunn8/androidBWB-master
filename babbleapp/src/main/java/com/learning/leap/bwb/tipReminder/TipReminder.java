package com.learning.leap.bwb.tipReminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.learning.leap.bwb.R;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class TipReminder {

    private int bucketNumber;
    private int numberOfTips;
    private Context context;

    public TipReminder(int bucketNumber, int numberOfTips, Context context) {
        this.bucketNumber = bucketNumber;
        this.numberOfTips = numberOfTips;
        this.context = context;
    }

    private void setReminder(Date notificationDate) {
        if (notificationDate == null) {
            return;
        }
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(notificationDate);
        now.setTime(new Date());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTime().before(now.getTime())) {
            calendar.add(Calendar.DATE, 1);
        }
        Log.d("test", convertDate(calendar.getTime()));
        setTipToAlarmManger(calendar.getTimeInMillis(), alarmIntent());
        Utility.addCustomEvent(Constant.TIP_SCHEUDLED_TIME, Utility.getUserID(context), null, context);
    }

    private PendingIntent alarmIntent() {
        Intent alarmIntent = new Intent(context, VoteNotificationBroadcastReciever.class);
        alarmIntent.putExtra("id", bucketNumber);
        alarmIntent.putExtra("NumberOfTips", numberOfTips);
        return PendingIntent.getBroadcast(context.getApplicationContext(), bucketNumber, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void cancelTipReminder() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent());
        }
    }

    private void setTipToAlarmManger(long timeInMills, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMills, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMills, pendingIntent);
            }
        }
    }

    private String convertDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    private Date convertStringToDate(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date date;
        try {
            date = simpleDateFormat.parse(time);
            Calendar tipReminderCalender = Calendar.getInstance();
            Calendar tipTime = Calendar.getInstance();
            if (date != null) {
                tipTime.setTime(date);
                tipReminderCalender.set(Calendar.HOUR_OF_DAY, tipTime.get(Calendar.HOUR_OF_DAY));
                tipReminderCalender.set(Calendar.MINUTE, tipTime.get(Calendar.MINUTE));
                tipReminderCalender.set(Calendar.SECOND, 0);
                tipReminderCalender.set(Calendar.MILLISECOND, 0);
                return tipReminderCalender.getTime();
            } else {
                return null;
            }

        } catch (ParseException ex) {
            return null;
        }
    }

    public void setAlarmForTip(int index) {
        if (bucketNumber == 1) {
            String[] allStartTimes = context.getResources().getStringArray(R.array.all_times_settings_array);
            String firstTip = allStartTimes[index];
            Date tipTime = convertStringToDate(firstTip);
            setReminder(tipTime);
        } else {
            String[] allEndTimes = context.getResources().getStringArray(R.array.all_times_settings_array);
            String secondTip = allEndTimes[index];
            Date endTime = convertStringToDate(secondTip);
            setReminder(endTime);
        }
    }

}
