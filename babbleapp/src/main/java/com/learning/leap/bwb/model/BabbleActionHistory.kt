package com.learning.leap.bwb.model

import android.annotation.SuppressLint
import android.content.Context
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.learning.leap.bwb.models.ActionHistory
import com.learning.leap.bwb.utility.Utility
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.realm.Realm
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@DynamoDBTable(tableName = "babbleActionHistory")
class BabbleActionHistory(
        @DynamoDBHashKey(attributeName = "ActionHistoryID")
        private val actionHistoryID: String,
        @DynamoDBAttribute(attributeName = "Created")
        private val created: String,
        @DynamoDBRangeKey(attributeName = "BabbleID")
        private val babbleID: String,
        @DynamoDBAttribute(attributeName = "ActionTime")
        private val actionTime: String,
        @DynamoDBAttribute(attributeName = "ActionMessage")
        private val actionMessage: String,
        @DynamoDBAttribute(attributeName = "NotificationID")
        private val notificationID: String,
        @DynamoDBAttribute(attributeName = "GroupCode")
        private val groupCode: String,
        private var mNotification: BabbleTip
) {

        fun createActionHistoryItem(babbleID: String, message: String?, tag: String?) {
                val realm = Realm.getDefaultInstance()
                val date = getDateString()
                realm.beginTransaction()
                val history = realm.createObject(ActionHistory::class.java)
                history.babbleID = babbleID
                history.actionHistoryID = date + babbleID
                history.actionMessage = message
                history.actionTime = date
                history.created = date
                if (tag != null) {
                        history.notificationID = date + tag
                } else {
                        history.notificationID = "None"
                }
                realm.copyToRealm(history)
                realm.commitTransaction()
        }

        fun uploadActionHistory(context: Context?): Disposable? {
                val amazonDynamoDBClient = AmazonDynamoDBClient(Utility.getCredientail(context))
                val mapper = DynamoDBMapper(amazonDynamoDBClient)
                return Observable.fromCallable {
                        val realm = Realm.getDefaultInstance()
                        realm.beginTransaction()
                        val query = realm.where(ActionHistory::class.java)
                        val histories = query.findAll()
                        if (histories.size >= 1) {
                                for (history in histories) {
                                        mapper.save(history)
                                }
                        }
                        histories.deleteAllFromRealm()
                        realm.commitTransaction()
                        Any()
                }.subscribe()
        }

        private fun getDateString(): String {
                @SuppressLint("SimpleDateFormat") val df: DateFormat =
                        SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                val date = Calendar.getInstance().time
                return df.format(date)
        }
}
