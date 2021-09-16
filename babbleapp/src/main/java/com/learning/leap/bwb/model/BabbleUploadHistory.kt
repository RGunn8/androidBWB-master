package com.learning.leap.bwb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable

@Entity
@DynamoDBTable(tableName = "babbleUploadHistory")
class BabbleUploadHistory(
    @DynamoDBHashKey(attributeName = "UploadHistoryID")
    @PrimaryKey
    var uploadHistoryID: String,
    @DynamoDBRangeKey(attributeName = "BabbleID")
    @ColumnInfo(name = "babbleID")
    var babbleID: String,
    @DynamoDBAttribute(attributeName = "UploadTime")
    @ColumnInfo(name = "UploadTime")
    var uploadTime: String,
    @DynamoDBAttribute(attributeName = "UploadType")
    @ColumnInfo(name = "UploadType")
    var uploadType: String
) {
}

