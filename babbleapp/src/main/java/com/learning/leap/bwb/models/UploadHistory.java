package com.learning.leap.bwb.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ryangunn on 9/21/16.
 */

@DynamoDBTable(tableName = "UploadHistory")
public class UploadHistory {
    public String mUploadHistoryID;
    public String mBabbleID;
    public String mUploadTime;
    public String mUploadType;


    @DynamoDBHashKey(attributeName = "UploadHistoryID")
    public String getUploadHistoryID() {
        return mUploadHistoryID;
    }

    public void setUploadHistoryID(String uploadHistoryID) {
        mUploadHistoryID = uploadHistoryID;
    }

    @DynamoDBRangeKey(attributeName = "BabbleID")
    public String getBabbleID() {
        return mBabbleID;
    }

    public void setBabbleID(String babbleID) {
        mBabbleID = babbleID;
    }

    @DynamoDBAttribute(attributeName = "UploadTime")
    public String getUploadTime() {
        return mUploadTime;
    }

    public void setUploadTime(String uploadTime) {
        mUploadTime = uploadTime;
    }

    @DynamoDBAttribute(attributeName = "UploadType")
    public String getUploadType() {
        return mUploadType;
    }

    public void setUploadType(String uploadType) {
        mUploadType = uploadType;
    }

}
