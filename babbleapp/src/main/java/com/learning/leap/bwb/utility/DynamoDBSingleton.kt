package com.learning.leap.bwb.utility

import android.content.Context
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient

class DynamoDBSingleton() {

    companion object {
        private var dynamoDB: DynamoDBMapper? = null
        fun getDynamoDB(context: Context): DynamoDBMapper {
            if (dynamoDB == null) {
                val amazonDynamoDBClient =
                    AmazonDynamoDBClient(Utility.getCredientail(context.applicationContext))
                return DynamoDBMapper.builder().dynamoDBClient(amazonDynamoDBClient).build()
            } else {
                return dynamoDB!!
            }
        }
    }
}