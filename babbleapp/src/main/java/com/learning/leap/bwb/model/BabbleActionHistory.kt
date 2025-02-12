package com.learning.leap.bwb.model

import android.annotation.SuppressLint
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import com.learning.leap.bwb.room.BabbleDatabase
import com.learning.leap.bwb.utility.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.first
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

@Entity
@DynamoDBTable(tableName = "babbleActionHistory")
class BabbleActionHistory(
    @DynamoDBHashKey(attributeName = "Id")
    val actionHistoryID: String,
    @DynamoDBAttribute(attributeName = "Created")
    @ColumnInfo(name = "created")
    val created: String,
    @DynamoDBAttribute(attributeName = "BabbleID")
    @ColumnInfo(name = "babbleID")
    val babbleID: String,
    @DynamoDBAttribute(attributeName = "ActionTime")
    @ColumnInfo(name = "actionTime")
    val actionTime: String,
    @DynamoDBAttribute(attributeName = "ActionMessage")
    @ColumnInfo(name = "actionMessage")
    val actionMessage: String,
    @DynamoDBAttribute(attributeName = "NotificationID")
    @ColumnInfo(name = "notificationID")
    val notificationID: String,
    @DynamoDBAttribute(attributeName = "GroupCode")
    @ColumnInfo(name = "groupCode")
    val groupCode: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
) {


    companion object {
        fun createActionHistoryItem(
            babbleID: String,
            message: String?,
            tag: String?,
            groupCode: String
        ): Disposable {
            val date = getDateString()
            val actionHistoryTag = if (tag != null) {
                date + tag
            } else {
                "None"
            }
            val actionHistory = BabbleActionHistory(
                date + babbleID,
                date,
                babbleID,
                date,
                message ?: "",
                actionHistoryTag,
                groupCode
            )
            return BabbleDatabase.getInstance().actionHistory().insertActionHistory(actionHistory)
                .subscribeOn(
                    Schedulers.io()
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

        suspend fun saveActionHistoryItem(
            database: BabbleDatabase,
            dataStore: DataStore<Preferences>,
            message: String?,
            tag: String?,
        ) {
            val pref = dataStore.data.first()
            val babbleID = pref[stringPreferencesKey(Constant.BABBLE_ID)] ?: ""
            val groupCode = pref[stringPreferencesKey(Constant.GROUP_CODE)] ?: ""
            val date = getDateString()
            val actionHistoryTag = if (tag != null) {
                date + tag
            } else {
                "None"
            }
            val actionHistory = BabbleActionHistory(
                date + babbleID,
                date,
                babbleID,
                date,
                message ?: "",
                actionHistoryTag,
                groupCode
            )
            database.actionHistory().insertActionHistory(actionHistory)
        }

        private fun getDateString(): String {
            @SuppressLint("SimpleDateFormat") val df: DateFormat =
                SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
            val date = Calendar.getInstance().time
            return df.format(date)
        }
    }

    @Ignore
    private var notification: BabbleTip? = null

}
