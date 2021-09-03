package com.learning.leap.bwb.models;

import android.app.AlarmManager;
import android.content.Context;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.learning.leap.bwb.PlayTodayJob;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.helper.ScheduleBucket;
import com.learning.leap.bwb.tipReminder.TipReminder;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.util.ArrayList;
import java.util.Arrays;


public class UserTipSettings {
    private static final String[] maxTips = {"2", "3", "4", "5", "6", "7"};
    private Context context;
    private int userNumberOfTipsIndex;
    private int startTimeIndex;
    private int endTimeIndex;
    private String[] startTimes;
    private String[] endTimes;
    private ArrayList<String> allTime;
    private Boolean sendTipsToday;

    public UserTipSettings(Context context) {
        this.context = context;
    }

    public void loadUserTipsSettings() {
        setStartTimes();
        setEndTimes();
        setAllTimes();
        loadUserNumberOfTipsIndexFromSharedPref();
        loadEndTimeIndexFromShartedPref();
        loadStartTimeIndexFromSharedPref();
    }

    private void loadStartTimeIndexFromSharedPref() {
        startTimeIndex = Utility.readIntSharedPreferences(Constant.START_TIME, context);
    }

    private void loadEndTimeIndexFromShartedPref() {
        endTimeIndex = Utility.readIntSharedPreferences(Constant.END_TIME, context);
    }

    private void loadUserNumberOfTipsIndexFromSharedPref() {
        userNumberOfTipsIndex = Utility.readIntSharedPreferences(Constant.NUM_OF_TIPS_INDEX, context);
    }

    private void setStartTimes() {
        startTimes = context.getResources().getStringArray(R.array.all_times_settings_array);
    }

    private void setEndTimes() {
        endTimes = context.getResources().getStringArray(R.array.all_times_settings_array);
    }

    private void setAllTimes() {
        String[] alltimes = context.getResources().getStringArray(R.array.all_times_settings_array);
        allTime = new ArrayList<>(Arrays.asList(alltimes));
    }

    private Boolean hidePlusButton(int index, int max) {
        return index == max;
    }

    private Boolean hideMinusButton(int index) {
        return index == 0;
    }

    public Boolean hideStartTimePlusButton() {
        return hidePlusButton(startTimeIndex, startTimes.length - 1);
    }

    public Boolean hideStartTimeMinusButton() {
        return hideMinusButton(startTimeIndex);
    }

    public Boolean hideEndTimePlusButton() {
        return hidePlusButton(endTimeIndex, endTimes.length - 1);
    }

    public Boolean hideEndTimeMinusButton() {
        return hideMinusButton(endTimeIndex);
    }

    public Boolean hideUserNumberOfTipsPlusButton() {
        return hidePlusButton(userNumberOfTipsIndex, maxTips.length - 1);
    }

    public Boolean hideUserNumberOfTipsMinusButton() {
        return hideMinusButton(userNumberOfTipsIndex);
    }

    private boolean minusButtonTapped(int index) {
        return index != 0;
    }

    public void startTimeMinusButtonHasBeenTapped() {
        if (minusButtonTapped(startTimeIndex)) {
            startTimeIndex--;
        }
    }

    public void startTimePlusButtonHasBeenTapped() {
        startTimeIndex++;
    }

    public void endTimeMinusButtonHasBeenTapped() {
        if (minusButtonTapped(endTimeIndex)) {
            endTimeIndex--;
        }
    }

    public void endTimePlusButtonHasBeenTapped() {
        endTimeIndex++;
    }

    public void userMaxNumberOfTipsMinusButtonHasBeenTapped() {
        if (minusButtonTapped(userNumberOfTipsIndex)) {
            userNumberOfTipsIndex--;
        }
    }

    public void userMaxNumberOfTipsPlusButtonHasBeenTapped() {
        userNumberOfTipsIndex++;
    }

    public String startTimeAtIndex() {
        return startTimes[startTimeIndex];
    }

    public String endTimeAtIndex() {
        return endTimes[endTimeIndex];
    }

    public void loadTurnOnTips() {
        sendTipsToday = Utility.readBoolSharedPreferences(Constant.SEND_TIPS_TODAY, context);
    }

    public void saveTurnOnTips(boolean firstTipOn, boolean secondTipOn) {
        boolean isFirstTipOn = Utility.readBoolSharedPreferences(Constant.TIP_ONE_ON, context);
        boolean isSecondTipOn = Utility.readBoolSharedPreferences(Constant.TIP_TWO_ON, context);
        int userMaxTipInt = Integer.parseInt(userMaxNumberOfTipsAtIndex());
        int oldEndTime = Utility.readIntSharedPreferences(Constant.END_TIME, context);
        int oldStartTime = Utility.readIntSharedPreferences(Constant.START_TIME, context);
        int oldMaxNumberOfTips = Utility.readIntSharedPreferences(Constant.TIPS_PER_DAY, context);
        if (isFirstTipOn != firstTipOn || oldStartTime != startTimeIndex || userMaxTipInt != oldMaxNumberOfTips) {
            TipReminder oldFirstTip = new TipReminder(1, oldMaxNumberOfTips, context);
            oldFirstTip.cancelTipReminder();
            TipReminder firstTip = new TipReminder(1, userMaxTipInt, context);
            if (firstTipOn) {
                firstTip.setAlarmForTip(startTimeIndex);
            } else {
                firstTip.cancelTipReminder();
            }
        }

        if (isSecondTipOn != secondTipOn || oldEndTime != endTimeIndex || userMaxTipInt != oldMaxNumberOfTips) {
            TipReminder oldSecondTip = new TipReminder(2, oldMaxNumberOfTips, context);
            oldSecondTip.cancelTipReminder();
            TipReminder secondTip = new TipReminder(2, userMaxTipInt, context);
            if (secondTipOn) {
                secondTip.setAlarmForTip(endTimeIndex);
            } else {
                secondTip.cancelTipReminder();
            }
        }

        Utility.writeBoolenSharedPreferences(Constant.TIP_ONE_ON, firstTipOn, context);
        Utility.writeBoolenSharedPreferences(Constant.TIP_TWO_ON, secondTipOn, context);
    }

    private int findEndTimeIndex() {
        return allTime.indexOf(endTimeAtIndex());
    }

    public void saveIndexes() {
        Utility.writeIntSharedPreferences(Constant.START_TIME, startTimeIndex, context);
        Utility.writeIntSharedPreferences(Constant.END_TIME, endTimeIndex, context);
        int userMaxTipInt = Integer.parseInt(userMaxNumberOfTipsAtIndex());
        Utility.writeIntSharedPreferences(Constant.NUM_OF_TIPS_INDEX, userNumberOfTipsIndex, context);
        Utility.writeIntSharedPreferences(Constant.TIPS_PER_DAY, userMaxTipInt, context);
    }

    public String userMaxNumberOfTipsAtIndex() {
        return maxTips[userNumberOfTipsIndex];
    }

    public Boolean getSendTipsToday() {
        return sendTipsToday;
    }

    public void setSendTipsToday(Boolean sendTipsToday) {
        this.sendTipsToday = sendTipsToday;
    }


}
