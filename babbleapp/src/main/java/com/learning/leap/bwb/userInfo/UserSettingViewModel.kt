package com.learning.leap.bwb.userInfo

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.model.BabbleTip
import com.learning.leap.bwb.model.BabbleUser
import com.learning.leap.bwb.room.BabbleDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class UserSettingViewModel @Inject constructor(
    private val mapper: DynamoDBMapper,
    private val sharedPreferences: SharedPreferences,
    private val localLoadSaveHelper: LocalLoadSaveHelper,
    private val babbleDatabase: BabbleDatabase
) : ViewModel() {

    private val _state = MutableLiveData(UserSettingState())
    val state: LiveData<UserSettingState> = _state

    private val _showDialog = MutableLiveData(false)
    val showDialog: LiveData<Boolean> = _showDialog

    private val _saveSuccessful = MutableLiveData(false)
    val saveSuccessful : LiveData<Boolean> = _saveSuccessful

    fun onNameChange(newName: String) {
        _state.postValue(state.value?.copy(userName = newName))
    }

    fun onGenderChange(newGender: Gender) {
        _state.postValue(state.value?.copy(gender = newGender))
    }

    fun onCodeChange(newCode: String) {
        _state.postValue(state.value?.copy(groupCode = newCode))
    }

    fun onBirthdayChanged(newBirthday: String) {
        _state.postValue(state.value?.copy(birthday = newBirthday))
    }

    private fun retrieveNotifications(babyAge: Int, mapper: DynamoDBMapper): PaginatedScanList<BabbleTip> {
        val scanExpression = DynamoDBScanExpression()
        scanExpression.limit = 2000
        val attributeValue = HashMap<String, AttributeValue>()
        val age = babyAge.toString()
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

    private fun updateViewAfterRetrievingNotificationList(notifications: List<BabbleTip>) {
        _state.postValue(state.value?.copy(loading = false ))
        if (notifications.isEmpty()) {
            _state.postValue(state.value?.copy(error = UserSettingError.None()))
        } else {
            saveNotifications(notifications)
        }
    }

    private fun saveNotifications(notifications: List<BabbleTip>) {
        viewModelScope.launch {
           updateTips(notifications)
        }
    }

    private suspend fun updateTips(notifications: List<BabbleTip>){
        withContext(Dispatchers.IO){
            localLoadSaveHelper.saveNotificationSize(notifications.size)
            babbleDatabase.babbleTipDAO().deleteAllTips()
            babbleDatabase.babbleTipDAO().insertAll(notifications)
            _saveSuccessful.postValue(true)
        }
    }

    fun saveBabbleUser(isNewUser: Boolean) {
        viewModelScope.launch {
            try {
                if (checkIfPlayerIsValid()) {
                    _state.postValue(state.value?.copy(error = UserSettingError.None()))
                    if (isNewUser) {
                        state.value?.let {
                            val user = BabbleUser(
                                UUID.randomUUID().toString(),
                                it.birthday,
                                it.userName,
                                it.gender.name,
                                it.groupCode
                            )
                            updateUser(user,isNewUser)
                        }

                    } else {
                        val user = babbleDatabase.babbleUserDAO().getUser()
                        state.value?.let {
                            val updateUser = user.copy(
                                babyBirthday = it.birthday,
                                babyName = it.userName,
                                babyGender = it.gender.name,
                                groupCode = it.groupCode
                            )
                            updateUser(updateUser,isNewUser)
                        }

                    }
                    saveCurrentBabblePlayerSharedPreference(localLoadSaveHelper, isNewUser)
                } else if (checkNameIsEmpty()) {
                    _state.postValue(state.value?.copy(error = UserSettingError.EmptyName()))
                    _showDialog.value = true
                } else if (checkNameIsTooLong()) {
                    _state.postValue(state.value?.copy(error = UserSettingError.LongName()))
                    _showDialog.value = true
                } else if (checkAge()) {
                    _state.postValue(state.value?.copy(error = UserSettingError.Age()))
                    _showDialog.value = true
                }

            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                _state.postValue(state.value?.copy(error = UserSettingError.Network()))
                _showDialog.value = true
            }
        }

    }

    private suspend fun updateUser(babbleUser: BabbleUser, isNewUser: Boolean){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                mapper.save(babbleUser)
                if (isNewUser) {
                    _state.postValue(state.value?.copy(loading =true ))
                    babbleDatabase.babbleUserDAO().addUser(babbleUser)
                    val tips = retrieveNotifications(babbleUser.userAgeInMonth,mapper)
                    updateViewAfterRetrievingNotificationList(tips.toList())
                }else{
                    babbleDatabase.babbleUserDAO().update(babbleUser)
                }
            }
        }
    }

    private fun saveCurrentBabblePlayerSharedPreference(
        saveHelper: LocalLoadSaveHelper,
        isNewUser: Boolean
    ) {
        state.value?.let {
            val ageInMonth = setUserAgeInMonth(it.birthday)
            saveHelper.saveBabyBirthDay(it.birthday)
            saveHelper.saveUserBirthDayInMonth(ageInMonth)
            saveHelper.saveBabyName(it.userName)
            saveHelper.saveBabyGender(it.gender.name)
            saveHelper.saveGroupCode(it.groupCode)
            val ageRangeBucket = getAgeRangeBucket(ageInMonth)
            saveHelper.saveAgeRangeBucket(ageRangeBucket)
            if (isNewUser) {
                saveHelper.saveBabbleID(UUID.randomUUID().toString())
            }
        }

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

    private fun checkName(): Boolean {
        return !checkNameIsEmpty() && !checkNameIsTooLong()
    }

    private fun checkNameIsEmpty(): Boolean {
        return state.value?.userName?.isEmpty() == true
    }

    private fun checkNameIsTooLong(): Boolean {
        state.value?.userName?.length?.let {
            return it >= 25
        } ?: kotlin.run {
            return false
        }
    }

    private fun checkAge(): Boolean {
        state.value?.let {
            return it.birthday.isNotEmpty() && setUserAgeInMonth(it.birthday) <= 0
        } ?: kotlin.run {
            return false
        }
    }

    private fun checkIfPlayerIsValid(): Boolean {
        state.value?.let {
            return checkName() && setUserAgeInMonth(it.birthday) > 0
        } ?: kotlin.run {
            return false
        }
    }


    private fun monthInBetween(birthdayDate: Date): Long {
        val today = Date()
        val todayLocalDate: LocalDate =
            Instant.ofEpochMilli(today.time).atZone(ZoneId.systemDefault()).toLocalDate()
        val birthdayLocalDate =
            Instant.ofEpochMilli(birthdayDate.time).atZone(ZoneId.systemDefault()).toLocalDate()
        return birthdayLocalDate.until(todayLocalDate, ChronoUnit.MONTHS)
    }

    private fun setUserAgeInMonth(babyBirthday: String): Int {
        val pattern = "M/dd/yyyy"
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        format.isLenient = false
        format.parse(babyBirthday)?.let {
            val userAgeInMonthDouble = monthInBetween(it)
            if (userAgeInMonthDouble > 0 && userAgeInMonthDouble < 1) {
                return 1
            }
            return floor(userAgeInMonthDouble.toDouble()).toInt()

        } ?: kotlin.run {
            return 0
        }
    }

    fun resetError() {
        _showDialog.value = false
    }

}