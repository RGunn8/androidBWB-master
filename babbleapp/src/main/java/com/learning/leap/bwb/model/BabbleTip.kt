package com.learning.leap.bwb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import java.util.regex.Pattern

@Entity
@DynamoDBTable(tableName = "babbleTips")
class BabbleTip {
    @DynamoDBRangeKey(attributeName = "Created")
    @ColumnInfo(name = "created")
    var created: String = ""

    @DynamoDBHashKey(attributeName = "Tag")
    @ColumnInfo(name = "tag")
    var tag: String = ""

    @DynamoDBAttribute(attributeName = "AgeRange")
    @ColumnInfo(name = "ageRange")
    var ageRange: String = ""

    @DynamoDBAttribute(attributeName = "Deleted")
    @ColumnInfo(name = "deleted")
    var deleted: String = ""

    @DynamoDBAttribute(attributeName = "EndMonth")
    @ColumnInfo(name = "endMonth")
    var endMonth: Int = 0

    @DynamoDBAttribute(attributeName = "Message")
    @ColumnInfo(name = "message")
    var message: String = ""

    @DynamoDBAttribute(attributeName = "SoundFileName")
    @ColumnInfo(name = "soundFileName")
    var soundFileName: String = ""

    @DynamoDBAttribute(attributeName = "StartMonth")
    @ColumnInfo(name = "startMonth")
    var startMonth: Int = 0

    @DynamoDBAttribute(attributeName = "VideoFileName")
    @ColumnInfo(name = "videoFileName")
    var videoFileName: String = ""

    @DynamoDBAttribute(attributeName = "Language")
    @ColumnInfo(name = "language")
    var language: String = ""

    @DynamoDBAttribute(attributeName = "Category")
    @ColumnInfo(name = "category")
    var category: String = ""

    @DynamoDBAttribute(attributeName = "Subcategory")
    @ColumnInfo(name = "subCategory")
    var subCategory: String = ""

    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "playToday")
    var playToday: Boolean = false

    constructor() {

    }

    public


    companion object {
//        @JvmStatic
//        fun getNotificationFromRealm(realm: Realm): Observable<RealmResults<BabbleTip>> {
//            return Observable.fromCallable(Callable {
//                realm.where(BabbleTip::class.java).findAll()
//            })
//        }

//        @JvmStatic
//        fun getTipsWithCategory(
//            category: String,
//            realm: Realm
//        ): Observable<RealmResults<BabbleTip>> {
//            return Observable.fromCallable(Callable {
//                realm.where(BabbleTip::class.java).equalTo("category", category).findAll()
//            })
//        }

//        @JvmStatic
//        fun getTipsWithSubcategory(
//            subCategory: String,
//            realm: Realm
//        ): Observable<RealmResults<BabbleTip>> {
//            return Observable.fromCallable(Callable {
//                realm.where(BabbleTip::class.java).equalTo("subcategory", subCategory).findAll()
//            })
//        }

//        @JvmStatic
//        fun getFavoriteTips(realm: Realm): Observable<RealmResults<BabbleTip>> {
//            return Observable.fromCallable(Callable {
//                realm.where(BabbleTip::class.java).equalTo("favorite", true).findAll()
//            })
//        }

//        @JvmStatic
//        fun getPlayTodayFromRealm(realm: Realm): Observable<RealmResults<BabbleTip>> {
//            return Observable.fromCallable(Callable {
//                realm.where(BabbleTip::class.java)
//                    .equalTo("playToday", true)
//                    .findAll()
//            })
//        }

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



