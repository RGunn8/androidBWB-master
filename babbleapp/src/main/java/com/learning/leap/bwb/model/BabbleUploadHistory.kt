package com.learning.leap.bwb.model

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable


@DynamoDBTable(tableName = "babbleUploadHistory")
data class BabbleUploadHistory(
    @DynamoDBHashKey(attributeName = "UploadHistoryID")
    var uploadHistoryID: String,
    @DynamoDBRangeKey(attributeName = "BabbleID")
    var babbleID: String,
    @DynamoDBAttribute(attributeName = "UploadTime")
    var uploadTime: String,
    @DynamoDBAttribute(attributeName = "UploadType")
    var uploadType: String
) {
}

