package com.learning.leap.bwb.model

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.concurrent.Callable
import java.util.regex.Pattern

@RealmClass
@DynamoDBTable(tableName = "babbleTips")
open class BabbleTip() : RealmModel {
    @DynamoDBRangeKey(attributeName = "Created")
    var created: String = ""

    @DynamoDBHashKey(attributeName = "Tag")
    var tag: String = ""

    @DynamoDBAttribute(attributeName = "AgeRange")
    var ageRange: String = ""

    @DynamoDBAttribute(attributeName = "Deleted")
    var deleted: String = ""

    @DynamoDBAttribute(attributeName = "EndMonth")
    var endMonth: Int = 0

    @DynamoDBAttribute(attributeName = "Message")
    var message: String = ""

    @DynamoDBAttribute(attributeName = "SoundFileName")
    var soundFileName: String = ""

    @DynamoDBAttribute(attributeName = "StartMonth")
    var startMonth: Int = 0

    @DynamoDBAttribute(attributeName = "VideoFileName")
    var videoFileName: String = ""

    @DynamoDBAttribute(attributeName = "Language")
    private var language: String = ""

    @DynamoDBAttribute(attributeName = "Category")
    var category: String = ""

    @DynamoDBAttribute(attributeName = "Subcategory")
    var subcategory: String = ""
    public var playToday: Boolean = false
    var favorite: Boolean = false

    @PrimaryKey
    var id: Int = 0

    companion object {
        @JvmStatic
        fun getNotificationFromRealm(realm: Realm): Observable<RealmResults<BabbleTip>> {
            return Observable.fromCallable(Callable {
                realm.where(BabbleTip::class.java).findAll()
            })
        }

        @JvmStatic
        fun getTipsWithCategory(
            category: String,
            realm: Realm
        ): Observable<RealmResults<BabbleTip>> {
            return Observable.fromCallable(Callable {
                realm.where(BabbleTip::class.java).equalTo("category", category).findAll()
            })
        }

        @JvmStatic
        fun getTipsWithSubcategory(
            subCategory: String,
            realm: Realm
        ): Observable<RealmResults<BabbleTip>> {
            return Observable.fromCallable(Callable {
                realm.where(BabbleTip::class.java).equalTo("subcategory", subCategory).findAll()
            })
        }

        @JvmStatic
        fun getFavoriteTips(realm: Realm): Observable<RealmResults<BabbleTip>> {
            return Observable.fromCallable(Callable {
                realm.where(BabbleTip::class.java).equalTo("favorite", true).findAll()
            })
        }

        @JvmStatic
        fun getPlayTodayFromRealm(realm: Realm): Observable<RealmResults<BabbleTip>> {
            return Observable.fromCallable(Callable {
                realm.where(BabbleTip::class.java)
                    .equalTo("playToday", true)
                    .findAll()
            })
        }

    }

    open fun noSoundFile(): Boolean {
        val fileName: String = soundFileName
        return fileName == "no file"
    }

    open fun noVideFile(): Boolean {
        val fileName: String = videoFileName
        return fileName == "no file"
    }

    open fun updateMessage(babyName: String): String {
        return message.replace("(?i)" + Pattern.quote("your baby").toRegex(), babyName)
            .replace("(?i)" + Pattern.quote("your child").toRegex(), babyName)
    }

}
