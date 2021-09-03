package com.learning.leap.bwb.model

import android.content.Context
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.models.BabblePlayer
import com.learning.leap.bwb.utility.Constant.updateAges
import io.realm.Realm
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

@RealmClass
@DynamoDBTable(tableName = "babbleUsers")
open class BabbleUser() : RealmModel {
    @PrimaryKey
    @DynamoDBHashKey(attributeName = "Id")
    var babbleID: String = ""

    @DynamoDBRangeKey(attributeName = "BabyBirthday")
    var babyBirthday: String = ""

    @DynamoDBAttribute(attributeName = "BabyName")
    var babyName: String = ""

    @DynamoDBAttribute(attributeName = "BabyGender")
    var babyGender: String = "Not Now"

    @DynamoDBAttribute(attributeName = "GroupCode")
    var groupCode: String = "None"

    var userAgeInMonth = 0
    var birthdayDate: Date? = null


    constructor(
        babbleID: String,
        babyBirthday: String,
        babyName: String,
        babyGender: String,
        groupCode: String = "none"
    ) : this() {
        this.babbleID = babbleID
        this.babyBirthday = babyBirthday
        this.babyName = babyName
        this.babyGender = babyGender
        this.groupCode = groupCode
        setUserAgeInMonth()
    }

    companion object {
        fun loadBabblePlayerFromSharedPref(saveHelper: LocalLoadSaveHelper): BabbleUser {
            return BabbleUser(
                saveHelper.babbleID,
                saveHelper.babyBirthDay,
                saveHelper.babyName,
                saveHelper.babyGender
            )
        }

        fun saveUpdatedInfo(context: Context?) {
            val saveHelper = LocalLoadSaveHelper(context)
            val sharedPrefBirthDay = saveHelper.babyBirthDay
            val updatedBabblePlayer = BabbleUser()
            updatedBabblePlayer.babyBirthday = sharedPrefBirthDay
            updatedBabblePlayer.setUserAgeInMonth()
            saveHelper.saveUserBirthDayInMonth(updatedBabblePlayer.userAgeInMonth)
        }

        fun homeScreenAgeCheck(context: Context?): Int {
            val saveHelper = LocalLoadSaveHelper(context)
            val sharedPrefBirthDay = saveHelper.babyBirthDay
            val updatedBabblePlayer = BabbleUser()
            updatedBabblePlayer.babyBirthday = sharedPrefBirthDay
            updatedBabblePlayer.setUserAgeInMonth()
            if (!saveHelper.checkedSaveBabyAged()) {
                saveHelper.saveUserBirthDayInMonth(updatedBabblePlayer.userAgeInMonth)
                saveHelper.updatedSavedBabyAged(true)
            }
            val sharedPrefBirthDayInMonth = saveHelper.userBirthdayInMonth
            return getAgeRangeBucket(sharedPrefBirthDayInMonth, updatedBabblePlayer)
        }

        private fun getAgeRangeBucket(
            sharedPrefBirthDayInMonth: Int,
            updatedBabblePlayer: BabbleUser
        ): Int {
            if (checkIfAgeIsInRange(0, 3, sharedPrefBirthDayInMonth)) {
                return 0
            } else if (checkIfAgeIsInRange(4, 6, sharedPrefBirthDayInMonth)) {
                return 1
            } else if (checkIfAgeIsInRange(7, 9, sharedPrefBirthDayInMonth)) {
                return 2
            } else if (checkIfAgeIsInRange(10, 12, sharedPrefBirthDayInMonth)) {
                return 3
            } else if (checkIfAgeIsInRange(13, 18, sharedPrefBirthDayInMonth)) {
                return 4
            } else if (checkIfAgeIsInRange(19, 24, sharedPrefBirthDayInMonth)) {
                return 5
            } else if (checkIfAgeIsInRange(25, 30, sharedPrefBirthDayInMonth)) {
                return 6
            } else if (checkIfAgeIsInRange(31, 36, sharedPrefBirthDayInMonth)) {
                return 7
            } else if (checkIfAgeIsInRange(37, 48, sharedPrefBirthDayInMonth)) {
                return 8
            } else {
                return 100
            }
        }

        private fun checkIfAgeIsInRange(startMonth: Int, endMonth: Int, age: Int): Boolean {
            return age in startMonth..endMonth
        }

    }

    fun getUserAge(context: Context): Int {
        val ageBucket = getAgeRangeBucket(userAgeInMonth, this)
        val saveHelper = LocalLoadSaveHelper(context)
        val saveAgeRangeBucket = saveHelper.ageRangeBucketNumber
        if (ageBucket != saveAgeRangeBucket) {
            return updateAges[saveAgeRangeBucket]
        } else {
            return updateAges[ageBucket]
        }
    }

    fun setUserAgeInMonth() {
        if (checkDate()) {
            val userAgeInMonthDouble = daysBetween(birthdayDate!!).toDouble() / 30
            if (userAgeInMonthDouble > 0 && userAgeInMonthDouble < 1) {
                userAgeInMonth = 1
                return
            }
            userAgeInMonth = floor(userAgeInMonthDouble).toInt()
        }
    }

    private fun checkName(): Boolean {
        return !checkNameIsEmpty()!! && !checkNameIsTooLong()!!
    }

    fun checkNameIsEmpty(): Boolean? {
        return babyName.isEmpty() || babyName == ""
    }

    fun checkNameIsTooLong(): Boolean? {
        return babyName.length >= 20
    }

    fun checkIfPlayerIsValid(): Boolean? {
        return checkDate() && checkName() && userAgeInMonth > 0
    }

    fun checkDate(): Boolean {
        val pattern = "MM/dd/yyyy"
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        format.isLenient = false
        try {
            format.parse(babyBirthday)?.let {
                birthdayDate = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }


    private fun updatedAgeCheck(value: Int): Boolean {
        return userAgeInMonth >= value
    }


    private fun daysBetween(birthday: Date): Float {
        val date = Date()
        return ((date.time - birthday.time) / (1000 * 60 * 60 * 24)).toFloat()
    }

    fun saveBabbleUser(mapper: DynamoDBMapper, saveHelper: LocalLoadSaveHelper) {
        mapper.save(this)
        saveCurrentBabblePlayerSharedPreference(saveHelper)
    }

    private fun saveCurrentBabblePlayerSharedPreference(saveHelper: LocalLoadSaveHelper) {
        saveHelper.saveBabyBirthDay(babyBirthday)
        saveHelper.saveUserBirthDayInMonth(userAgeInMonth)
        saveHelper.saveBabyName(babyName)
        saveHelper.saveBabbleID(babbleID)
        saveHelper.saveBabyGender(babyGender)
        saveHelper.saveGroupCode(groupCode)
        val ageRangeBucket = getAgeRangeBucket(userAgeInMonth, this)
        saveHelper.saveAgeRangeBucket(ageRangeBucket)
    }

//    fun savePlayerObservable(mapper: DynamoDBMapper, checker: NetworkCheckerInterface, saveHelper: LocalLoadSaveHelper) {
//        return
//    }


    fun retrieveNotifications(babyAge: Int, mapper: DynamoDBMapper): PaginatedScanList<BabbleTip> {
        val scanExpression = DynamoDBScanExpression()
        scanExpression.limit = 2000
        val attributeValue = HashMap<String, AttributeValue>()
        val age = Integer.toString(babyAge)
        val falseAttributeValue = AttributeValue()
        falseAttributeValue.s = "false"
        val ageAttributeValue = AttributeValue()
        ageAttributeValue.n = age
        attributeValue[":val"] = ageAttributeValue
        attributeValue[":val2"] = falseAttributeValue
        scanExpression.expressionAttributeValues = attributeValue
        scanExpression.filterExpression = "StartMonth<=:val AND EndMonth>=:val AND Deleted=:val2"
        return mapper.scan(BabbleTip::class.java, scanExpression)

    }

    fun saveUpdatedInfo(context: Context?) {
        val saveHelper = LocalLoadSaveHelper(context)
        val sharedPrefBirthDay = saveHelper.babyBirthDay
        val updatedBabblePlayer = BabblePlayer()
        updatedBabblePlayer.babyBirthday = sharedPrefBirthDay
        updatedBabblePlayer.setuserAgeInMonth()
        saveHelper.saveUserBirthDayInMonth(updatedBabblePlayer.getuserAgeInMonth())
    }

    fun savePlayerToRealm() {
        Realm.getDefaultInstance().copyToRealmOrUpdate(this)
    }

}


