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
data class BabbleTip (
    @DynamoDBRangeKey(attributeName = "Created")
    @ColumnInfo(name = "created")
    var created: String = "",

    @DynamoDBHashKey(attributeName = "Tag")
    @ColumnInfo(name = "tag")
    var tag: String = "",

    @DynamoDBAttribute(attributeName = "AgeRange")
    @ColumnInfo(name = "ageRange")
    var ageRange: String = "",

    @DynamoDBAttribute(attributeName = "Deleted")
    @ColumnInfo(name = "deleted")
    var deleted: String = "",

    @DynamoDBAttribute(attributeName = "EndMonth")
    @ColumnInfo(name = "endMonth")
    var endMonth: Int = 0,

    @DynamoDBAttribute(attributeName = "Message")
    @ColumnInfo(name = "message")
    var message: String = "",

    @DynamoDBAttribute(attributeName = "SoundFileName")
    @ColumnInfo(name = "soundFileName")
    var soundFileName: String = "",

    @DynamoDBAttribute(attributeName = "StartMonth")
    @ColumnInfo(name = "startMonth")
    var startMonth: Int = 0,

    @DynamoDBAttribute(attributeName = "VideoFileName")
    @ColumnInfo(name = "videoFileName")
    var videoFileName: String = "",

    @DynamoDBAttribute(attributeName = "Language")
    @ColumnInfo(name = "language")
    var language: String = "",

    @DynamoDBAttribute(attributeName = "Category")
    @ColumnInfo(name = "category")
    var category: String = "",

    @DynamoDBAttribute(attributeName = "Subcategory")
    @ColumnInfo(name = "subCategory")
    var subCategory: String = "",

    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false,

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "playToday")
    var playToday: Boolean = false
){

    fun noSoundFile(): Boolean {
        val fileName: String = soundFileName
        return fileName == "no file"
    }

    fun noVideFile(): Boolean {
        val fileName: String = videoFileName
        return fileName == "no file"
    }

    fun updateMessage(babyName: String): String {
        return message.replace("your baby",babyName,true).replace("your child",babyName,true)
    }

    fun getSoundFile():String{
        return this.created + "-" + this.soundFileName
    }

    fun getVideoFile():String{
        return this.created + "-" + this.videoFileName
    }
}



