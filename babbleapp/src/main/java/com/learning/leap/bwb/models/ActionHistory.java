package com.learning.leap.bwb.models;

import android.annotation.SuppressLint;
import android.content.Context;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.learning.leap.bwb.utility.Utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;


@DynamoDBTable(tableName = "babbleActionHistory")
public class ActionHistory extends RealmObject {
    private String mActionHistoryID;
    private String mCreated;
    private String mBabbleID;
    private String mActionTime;
    private String mActionMessage;
    private String mNotificationID;
    private String groupCode;

    public ActionHistory() {

    }

    public static void createActionHistoryItem(String babbleID, String message, String tag, String groupCode) {
        Realm realm = Realm.getDefaultInstance();
        String date = getDateString();
        realm.beginTransaction();
        ActionHistory history = realm.createObject(ActionHistory.class);
        history.setBabbleID(babbleID);
        history.setGroupCode(groupCode);
        history.setActionHistoryID(date + babbleID);
        history.setActionMessage(message);
        history.setActionTime(date);
        history.setCreated(date);
        if (tag != null) {
            history.setNotificationID(date + tag);
        } else {
            history.setNotificationID("None");
        }
        realm.copyToRealm(history);
        realm.commitTransaction();
    }

    public static Disposable uploadActionHistory(Context context) {

        AmazonDynamoDBClient amazonDynamoDBClient = new AmazonDynamoDBClient(Utility.getCredientail(context));
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
        return Observable.fromCallable(() -> {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmQuery<ActionHistory> query = realm.where(ActionHistory.class);
            RealmResults<ActionHistory> histories = query.findAll();
            if (histories.size() >= 1) {
                for (ActionHistory history : histories) {
                    mapper.save(history);
                }
            }
            histories.deleteAllFromRealm();
            realm.commitTransaction();
            return new Object();
        }).subscribe();

    }

    private static String getDateString() {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = Calendar.getInstance().getTime();
        return df.format(date);
    }

    @DynamoDBHashKey(attributeName = "Id")
    public String getActionHistoryID() {
        return mActionHistoryID;
    }

    public void setActionHistoryID(String actionHistoryID) {
        mActionHistoryID = actionHistoryID;
    }

    @DynamoDBAttribute(attributeName = "Created")
    public String getCreated() {
        return mCreated;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    @DynamoDBAttribute(attributeName = "BabbleID")
    public String getBabbleID() {
        return mBabbleID;
    }

    public void setBabbleID(String babbleID) {
        mBabbleID = babbleID;
    }

    @DynamoDBAttribute(attributeName = "ActionTime")
    public String getActionTime() {
        return mActionTime;
    }

    public void setActionTime(String actionTime) {
        mActionTime = actionTime;
    }

    @DynamoDBAttribute(attributeName = "ActionMessage")
    public String getActionMessage() {
        return mActionMessage;
    }

    public void setActionMessage(String actionMessage) {
        mActionMessage = actionMessage;
    }

    @DynamoDBAttribute(attributeName = "NotificationID")
    public String getNotificationID() {
        return mNotificationID;
    }

    public void setNotificationID(String notificationID) {
        mNotificationID = notificationID;
    }

    @DynamoDBAttribute(attributeName = "GroupCode")
    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
