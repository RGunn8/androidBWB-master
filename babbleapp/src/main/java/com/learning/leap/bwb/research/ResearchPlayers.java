package com.learning.leap.bwb.research;

import android.content.Context;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.learning.leap.bwb.Player;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.models.Notification;
import com.learning.leap.bwb.utility.NetworkCheckerInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ryanlgunn8 on 5/31/17.
 */

@DynamoDBTable(tableName = "ResearchBabblePlayers")
public class ResearchPlayers extends RealmObject implements Player {
    @PrimaryKey
    private String mBabbleID;
    private String mBabyBirthday;
    private String mBabyName;
    private int mZipCode = 10000;
    private int userAgeInMonth;
    private Date birthdayDate;
    private String babyGender = "Not Now";

    public ResearchPlayers() {

    }

    public static void saveUpdatedInfo(Context context) {
        LocalLoadSaveHelper saveHelper = new LocalLoadSaveHelper(context);
        String sharedPrefBirthDay = saveHelper.getBabyBirthDay();
        BabblePlayer updatedBabblePlayer = new BabblePlayer();
        updatedBabblePlayer.setBabyBirthday(sharedPrefBirthDay);
        updatedBabblePlayer.setuserAgeInMonth();
        saveHelper.saveUserBirthDayInMonth(updatedBabblePlayer.getuserAgeInMonth());
    }

    private static Boolean checkIfAgeIsInRange(int startMonth, int endMonth, int age) {
        return startMonth <= age && endMonth >= age;
    }

    @DynamoDBHashKey(attributeName = "BabbleID")
    public String getBabbleID() {
        return mBabbleID;
    }

    public void setBabbleID(String babbleID) {
        mBabbleID = babbleID;
    }

    @DynamoDBRangeKey(attributeName = "BabyBirthday")
    public String getBabyBirthday() {
        return mBabyBirthday;
    }

    public void setBabyBirthday(String babyBirthday) {
        mBabyBirthday = babyBirthday;
    }

    @DynamoDBAttribute(attributeName = "BabyName")
    public String getBabyName() {
        return mBabyName;
    }

    public void setBabyName(String babyName) {
        mBabyName = babyName;
    }

    public int getZipCode() {
        return mZipCode;
    }

    public void setZipCode(int zipCode) {
        mZipCode = zipCode;
    }

    @DynamoDBAttribute(attributeName = "BabyGender")
    public String getBabyGender() {
        return babyGender;
    }

    public void setBabyGender(String babyGender) {
        this.babyGender = babyGender;
    }

    public int getuserAgeInMonth() {
        return userAgeInMonth;
    }

    public void setuserAgeInMonth() {
        if (checkDate()) {
            double userAgeInMonthDouble = (double) daysBetween(this.birthdayDate) / 30;
            this.userAgeInMonth = (int) Math.floor(userAgeInMonthDouble);
        }

    }

    private Date getBirthdayDate() {
        return birthdayDate;
    }

    private void setBirthdayDate(Date birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    private void saveBabbleUser(DynamoDBMapper mapper, LocalLoadSaveHelper saveHelper) {
        mapper.save(this);
        saveCurrentBabblePlayerSharedPreference(saveHelper);
    }

    private void saveCurrentBabblePlayerSharedPreference(LocalLoadSaveHelper saveHelper) {
        saveHelper.saveBabyBirthDay(this.getBabyBirthday());
        saveHelper.saveUserBirthDayInMonth(this.getuserAgeInMonth());
        saveHelper.saveBabyName(this.getBabyName());
        saveHelper.saveBabbleID(this.getBabbleID());
        saveHelper.saveZipCode(this.getZipCode());
        saveHelper.saveBabyGender(this.getBabyGender());

    }

    @Override
    public void savePlayerToRealm() {
        Realm.getDefaultInstance().copyToRealmOrUpdate(this);
    }

    public Observable<Object> savePlayerObservable(DynamoDBMapper mapper, NetworkCheckerInterface checker, LocalLoadSaveHelper saveHelper) {

        return Observable.fromCallable(() -> {
            if (checker.isConnected()) {
                saveBabbleUser(mapper, saveHelper);
            } else {
                throw new Exception();
            }
            return new Object();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public Observable<PaginatedScanList<Notification>> retriveNotifications(int babyAge, DynamoDBMapper mapper) {
        return Observable.fromCallable(() -> {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            scanExpression.setLimit(2000);
            HashMap<String, AttributeValue> attributeValue = new HashMap<>();
            String age = Integer.toString(babyAge);
            AttributeValue falseAttributeValue = new AttributeValue();
            falseAttributeValue.setS("false");
            AttributeValue ageAttributeValue = new AttributeValue();
            ageAttributeValue.setN(age);
            attributeValue.put(":val", ageAttributeValue);
            attributeValue.put(":val2", falseAttributeValue);
            scanExpression.setExpressionAttributeValues(attributeValue);
            scanExpression.setFilterExpression("StartMonthNumber<=:val AND EndMonthNumber>=:val AND Deleted=:val2");
            return mapper.scan(Notification.class, scanExpression);
        });
    }
//    public static boolean homeScreenAgeCheck(Context context){
//        LocalLoadSaveHelper saveHelper = new LocalLoadSaveHelper(context);
//        String sharedPrefBirthDay = saveHelper.getBabyBirthDay();
//        BabblePlayer updatedBabblePlayer = new BabblePlayer();
//        updatedBabblePlayer.setBabyBirthday(sharedPrefBirthDay);
//        updatedBabblePlayer.setuserAgeInMonth();
//        if (!saveHelper.checkedSaveBabyAged()){
//            saveHelper.saveUserBirthDayInMonth(updatedBabblePlayer.getuserAgeInMonth());
//            saveHelper.updatedSavedBabyAged(true);
//        }
//        int sharedPrefBirthDayInMonth = saveHelper.getUserBirthdayInMonth();
//        if (checkIfAgeIsInRange(0,3,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(4)){
//            return true;
//        }else if (checkIfAgeIsInRange(4,6,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(7)){
//            return true;
//        }else if (checkIfAgeIsInRange(7,9,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(10)){
//            return true;
//        }else if (checkIfAgeIsInRange(10,12,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(13)){
//            return true;
//        }else if (checkIfAgeIsInRange(13,18,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(19)){
//            return true;
//        }else if (checkIfAgeIsInRange(19,24,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(25)){
//            return true;
//        }else if (checkIfAgeIsInRange(25,30,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(31)){
//            return true;
//        }else if (checkIfAgeIsInRange(31,36,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(37)){
//            return true;
//        }else if (checkIfAgeIsInRange(37,48,sharedPrefBirthDayInMonth) && updatedBabblePlayer.updatedAgeCheck(49)){
//            return true;
//        }else {
//            return false;
//        }
//
//    }

    public Observable<PaginatedScanList<ResearchNotifications>> retriveNorthWestenNotifications(DynamoDBMapper mapper) {
        return Observable.fromCallable(() -> {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            scanExpression.setLimit(2000);
            HashMap<String, AttributeValue> attributeValue = new HashMap<>();
            AttributeValue falseAttributeValue = new AttributeValue();
            falseAttributeValue.setS("false");
            attributeValue.put(":val2", falseAttributeValue);
            scanExpression.setExpressionAttributeValues(attributeValue);
            scanExpression.setFilterExpression("Deleted=:val2");
            return mapper.scan(ResearchNotifications.class, scanExpression);
        });
    }

    private Boolean updatedAgeCheck(int value) {
        return this.getuserAgeInMonth() >= value;
    }


    public float daysBetween(Date birthday) {
        Date date = new Date();
        return (float) ((date.getTime() - birthday.getTime()) / (1000 * 60 * 60 * 24));
    }


    public boolean checkDate() {
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        try {
            this.setBirthdayDate(format.parse(this.getBabyBirthday()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Player loadBabblePlayerFronSharedPref(LocalLoadSaveHelper saveHelper) {
        Player babblePlayer = new ResearchPlayers();
        babblePlayer.setBabyBirthday(saveHelper.getBabyBirthDay());
        babblePlayer.setBabyName(saveHelper.getBabyName());
        babblePlayer.setZipCode(saveHelper.getZipCode());
        babblePlayer.setBabyGender(saveHelper.getBabyGender());
        return babblePlayer;
    }


    public boolean checkZipCode() {
        try {
            return Integer.toString(this.getZipCode()).matches("[0-9]{5}");
        } catch (NumberFormatException e) {
            return false;
        }

    }

    private boolean checkName() {
        return !checkNameIsEmpty() && !checkNameIsTooLong();
    }

    public Boolean checkNameIsEmpty() {
        return this.getBabyName().isEmpty() || getBabyName().equals("");
    }

    public Boolean checkNameIsTooLong() {
        return this.getBabyName().length() >= 20;
    }

    public Boolean checkIfPlayerIsValid() {
        return checkDate() && checkName() && this.getuserAgeInMonth() > 0;
    }

}
