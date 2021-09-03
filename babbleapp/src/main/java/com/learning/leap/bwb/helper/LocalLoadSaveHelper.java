package com.learning.leap.bwb.helper;

import android.content.Context;

import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LocalLoadSaveHelper {

    public Context context;

    public LocalLoadSaveHelper(Context context) {
        this.context = context;
    }

    public static Date getLastDayTurnedOff(Context context) throws Exception {
        String date = Utility.readStringSharedPreferences(Constant.LAST_DAY_TURNED_OFF, context);
        return new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(date);
    }

    public static void SaveLastDayTurnedOff(Boolean clear, Context context) {
        String date;
        if (clear) {
            date = null;
        } else {
            date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        }

        Utility.writeStringSharedPreferences(Constant.LAST_DAY_TURNED_OFF, date, context);
    }

    public void clearBabbleID() {
        Utility.writeStringSharedPreferences(Constant.BABBLE_ID, "", context);
    }

    public String getBabbleID() {
        return Utility.readStringSharedPreferences(Constant.BABBLE_ID, context);
    }

    public void saveBabbleID(String babbleID) {
        Utility.writeStringSharedPreferences(Constant.BABBLE_ID, babbleID, context);
    }

    public String getBabyBirthDay() {
        return Utility.readStringSharedPreferences(Constant.BABY_BIRTHDAY, context);
    }

    public void saveBabyBirthDay(String babyBirthDay) {
        Utility.writeStringSharedPreferences(Constant.BABY_BIRTHDAY, babyBirthDay, context);
    }

    public boolean checkedSaveBabyAged() {
        return Utility.readBoolSharedPreferences(Constant.SAVED_BABY_AGE, context);
    }

    public String getGroupCode() {
        return Utility.readStringSharedPreferences(Constant.GROUP_CODE, context);
    }

    public void updatedSavedBabyAged(boolean savedBabyAged) {
        Utility.writeBoolenSharedPreferences(Constant.SAVED_BABY_AGE, savedBabyAged, context);
    }

    public int getZipCode() {
        return Utility.readIntSharedPreferences(Constant.ZIP_CODE, context);
    }

    public void saveUserBirthDayInMonth(int month) {
        Utility.writeIntSharedPreferences(Constant.USER_BDAY_IN_MONTH, month, context);
    }

    public int getUserBirthdayInMonth() {
        return Utility.readIntSharedPreferences(Constant.USER_BDAY_IN_MONTH, context);
    }

    public void saveZipCode(int zipCode) {
        Utility.writeIntSharedPreferences(Constant.ZIP_CODE, zipCode, context);
    }

    public String getBabyName() {
        return Utility.readStringSharedPreferences(Constant.BABY_NAME, context);
    }

    public void saveBabyName(String babyName) {
        Utility.writeStringSharedPreferences(Constant.BABY_NAME, babyName, context);
    }

    public void saveGroupCode(String groupCode) {
        Utility.writeStringSharedPreferences(Constant.GROUP_CODE, groupCode, context);
    }

    public void saveBabyGender(String gender) {
        Utility.writeStringSharedPreferences(Constant.GENDER, gender, context);
    }

    public String getBabyGender() {
        return Utility.readStringSharedPreferences(Constant.GENDER, context);
    }

    public void saveAgeRangeBucket(int bucketNumber) {
        Utility.writeIntSharedPreferences(Constant.AGE_RANGE_BUCKET, bucketNumber, context);
    }

    public int getAgeRangeBucketNumber() {
        return Utility.readIntSharedPreferences(Constant.AGE_RANGE_BUCKET, context);
    }


    public void saveNotificationSize(int size) {
        Utility.writeIntSharedPreferences(Constant.NOTIFICATION_SIZE, size, context);
    }

    public int getNotificationTotalSize() {
        return Utility.readIntSharedPreferences(Constant.NOTIFICATION_SIZE, context);
    }


}
