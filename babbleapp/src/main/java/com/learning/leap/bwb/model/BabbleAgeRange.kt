package com.learning.leap.bwb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable

@Entity
@DynamoDBTable(tableName = "babbleAgeRanges")
class BabbleAgeRange(

    @DynamoDBAttribute(attributeName = "AgeRange")
    @ColumnInfo(name = "ageRange")
    var ageRange: String,

    @DynamoDBAttribute(attributeName = "StartMonth")
    @PrimaryKey
    var startMonth: Int,

    @DynamoDBAttribute(attributeName = "EndMonth")
    @ColumnInfo(name = "endMonth")
    var endMonth: Int,
)