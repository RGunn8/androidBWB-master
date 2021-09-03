package com.learning.leap.bwb.research;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.learning.leap.bwb.models.Notification;

import java.util.ArrayList;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ryanlgunn8 on 6/1/17.
 */

@DynamoDBTable(tableName = "ResearchNotifications")
public class ResearchNotifications extends RealmObject {
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

    public ResearchNotifications() {

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

    private Notification convertToNotificaion() {
        Notification notification = new Notification();
        notification.setCreated(getCreated());
        notification.setTag(getTag());
        notification.setMessage(getMessage());
        notification.setId(getId());
        notification.setSoundFileName(getSoundFileName());
        notification.setVideoFileName(getVideoFileName());
        return notification;
    }

    public Observable<RealmResults<Notification>> getNotificationFromRealm(Realm realm) {
        return Observable.fromCallable(() -> {
            if (realm.where(Notification.class).findAll().size() == 0) {
                RealmResults<ResearchNotifications> realmResults = realm.where(ResearchNotifications.class).findAll();
                ArrayList<Notification> notifications = new ArrayList<Notification>();
                for (ResearchNotifications not : realmResults) {
                    notifications.add(not.convertToNotificaion());
                }
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(notifications);
                realm.commitTransaction();
            }

            return realm.where(Notification.class).findAll();
        });
    }

}
