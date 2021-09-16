package com.learning.leap.bwb.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.learning.leap.bwb.Player.birthdayDate
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.utility.Constant.updateAges
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

@Entity
@DynamoDBTable(tableName = "babbleUsers")
data class BabbleUser(

    @ColumnInfo(name = "babbleID")
    @DynamoDBHashKey(attributeName = "Id")
    var babbleID: String,

    @DynamoDBRangeKey(attributeName = "BabyBirthday")
    @ColumnInfo(name = "babyBirthday")
    var babyBirthday: String,

    @DynamoDBAttribute(attributeName = "BabyName")
    @ColumnInfo(name = "babyName")
    var babyName: String,

    @DynamoDBAttribute(attributeName = "BabyGender")
    @ColumnInfo(name = "babyGender")
    var babyGender: String = "Not Now",

    @DynamoDBAttribute(attributeName = "GroupCode")
    @ColumnInfo(name = "groupCode")
    var groupCode: String = "None",

    ) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var userAgeInMonth: Int = 0

    @Ignore
    var birthdayDate: Date? = null

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
            val userAgeInMonth = setUserAgeInMonth(sharedPrefBirthDay)

            saveHelper.saveUserBirthDayInMonth(userAgeInMonth)
        }

        fun setUserAgeInMonth(babyBirthday: String): Int {
            val pattern = "MM/dd/yyyy"
            val format = SimpleDateFormat(pattern, Locale.getDefault())
            format.isLenient = false
            format.parse(babyBirthday)?.let {
                val userAgeInMonthDouble = daysBetween(it).toDouble() / 30
                if (userAgeInMonthDouble > 0 && userAgeInMonthDouble < 1) {
                    return 1
                }
                return floor(userAgeInMonthDouble).toInt()

            } ?: kotlin.run {
                return 0
            }
        }

        private fun daysBetween(birthdayDate: Date): Float {
            val date = Date()
            return ((date.time - birthdayDate.time) / (1000 * 60 * 60 * 24)).toFloat()
        }

        fun checkDate(babyBirthday: String): Date? {
            val pattern = "MM/dd/yyyy"
            val format = SimpleDateFormat(pattern, Locale.getDefault())
            format.isLenient = false
            try {
                format.parse(babyBirthday)?.let {
                    return birthdayDate
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
            return null
        }

        fun homeScreenAgeCheck(context: Context?): Int {
            val saveHelper = LocalLoadSaveHelper(context)
            val sharedPrefBirthDay = saveHelper.babyBirthDay
            val userAgeInMonth = setUserAgeInMonth(sharedPrefBirthDay)
            if (!saveHelper.checkedSaveBabyAged()) {
                saveHelper.saveUserBirthDayInMonth(userAgeInMonth)
                saveHelper.updatedSavedBabyAged(true)
            }
            val sharedPrefBirthDayInMonth = saveHelper.userBirthdayInMonth
            return getAgeRangeBucket(sharedPrefBirthDayInMonth)
        }

        private fun getAgeRangeBucket(
            sharedPrefBirthDayInMonth: Int,
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
        val ageBucket = getAgeRangeBucket(userAgeInMonth)
        val saveHelper = LocalLoadSaveHelper(context)
        val saveAgeRangeBucket = saveHelper.ageRangeBucketNumber
        if (ageBucket != saveAgeRangeBucket) {
            return updateAges[saveAgeRangeBucket]
        } else {
            return updateAges[ageBucket]
        }
    }



    private fun checkName(): Boolean {
        return !checkNameIsEmpty() && !checkNameIsTooLong()!!
    }

    fun checkNameIsEmpty(): Boolean {
        return babyName.isEmpty() || babyName == ""
    }

    fun checkNameIsTooLong(): Boolean {
        return babyName.length >= 20
    }

    fun checkIfPlayerIsValid(): Boolean {
        return checkDate(this.babyBirthday) != null && checkName() && userAgeInMonth > 0
    }


    private fun updatedAgeCheck(value: Int): Boolean {
        return userAgeInMonth >= value
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
        val ageRangeBucket = getAgeRangeBucket(userAgeInMonth)
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
        // val updatedBabblePlayer = BabbleUser
//        updatedBabblePlayer.babyBirthday = sharedPrefBirthDay
//        updatedBabblePlayer.setuserAgeInMonth()
        // saveHelper.saveUserBirthDayInMonth(updatedBabblePlayer.getuserAgeInMonth())
    }

    fun savePlayerToRealm() {
        //Realm.getDefaultInstance().copyToRealmOrUpdate(this)
    }

}


