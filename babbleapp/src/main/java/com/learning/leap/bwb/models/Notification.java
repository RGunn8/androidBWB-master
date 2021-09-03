package com.learning.leap.bwb.models;

import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;

import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.internal.Context;

@DynamoDBTable(tableName = "Notifications")
public class Notification extends RealmObject {
    private String mCreated;
    private String mTag;
    private String mAgeRange;
    private String mDeleted;
    private String mEndMonth;
    private String mMessage;
    private String mSoundFileName;
    private String mStartMonth;
    private String mVideoFileName;
    private Boolean mPlayToday = false;
    private Boolean mFavorite = false;
    @PrimaryKey
    private int id;

    public Notification() {

    }

    @DynamoDBRangeKey(attributeName = "Created")
    public String getCreated() {
        return mCreated;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    @DynamoDBHashKey(attributeName = "Tag")
    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    @DynamoDBAttribute(attributeName = "AgeRange")
    public String getAgeRange() {
        return mAgeRange;
    }

    public void setAgeRange(String ageRange) {
        mAgeRange = ageRange;
    }

    @DynamoDBAttribute(attributeName = "Deleted")
    public String getDeleted() {
        return mDeleted;
    }

    public void setDeleted(String deleted) {
        mDeleted = deleted;
    }

    @DynamoDBAttribute(attributeName = "EndMonth")
    public String getEndMonth() {
        return mEndMonth;
    }

    public void setEndMonth(String endMonth) {
        mEndMonth = endMonth;
    }

    @DynamoDBAttribute(attributeName = "Message")
    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    @DynamoDBAttribute(attributeName = "SoundFileName")
    public String getSoundFileName() {
        return mSoundFileName;
    }

    public void setSoundFileName(String soundFileName) {
        mSoundFileName = soundFileName;
    }

    @DynamoDBAttribute(attributeName = "StartMonth")
    public String getStartMonth() {
        return mStartMonth;
    }

    public void setStartMonth(String startMonth) {
        mStartMonth = startMonth;
    }

    @DynamoDBAttribute(attributeName = "VideoFileName")
    public String getVideoFileName() {
        return mVideoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        mVideoFileName = videoFileName;
    }

    public Boolean getFavorite() {
        return mFavorite;
    }

    public void setFavorite(Boolean favorite) {
        mFavorite = favorite;
    }

    public Boolean getPlayToday() {
        return mPlayToday;
    }

    public void setPlayToday(Boolean playToday) {
        mPlayToday = playToday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean noSoundFile() {
        String fileName = this.getSoundFileName();
        return (fileName.equals("no file"));
    }

    public Boolean noVideFile() {
        String fileName = this.getVideoFileName();
        return (fileName.equals("no file"));
    }

    public String updateMessage(String babyName) {

        return mMessage.replaceAll("(?i)" + Pattern.quote("your baby"), babyName)
                .replaceAll("(?i)" + Pattern.quote("your child"), babyName);
    }

    public Observable<RealmResults<Notification>> getNotificationFromRealm(Realm realm) {
        return Observable.fromCallable(() -> realm.where(Notification.class).findAll());
    }

    public Observable<RealmResults<Notification>> getPlayTodayFromRealm(Realm realm) {
        return Observable.fromCallable(() -> realm.where(Notification.class)
                .equalTo("mPlayToday", true)
                .findAll());
    }

}
