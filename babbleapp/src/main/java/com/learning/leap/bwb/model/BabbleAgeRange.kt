package com.learning.leap.bwb.model

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
@DynamoDBTable(tableName = "babbleAgeRanges")
open class BabbleAgeRange : RealmModel {

    @DynamoDBAttribute(attributeName = "AgeRange")
    var ageRange: String = ""

    @DynamoDBAttribute(attributeName = "StartMonth")
    @PrimaryKey
    var startMonth: Int = 0

    @DynamoDBAttribute(attributeName = "EndMonth")
    var endMonth: Int = 0
}