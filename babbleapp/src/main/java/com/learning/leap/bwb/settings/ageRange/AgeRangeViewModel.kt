package com.learning.leap.bwb.settings.ageRange

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.learning.leap.bwb.model.BabbleTip
import com.learning.leap.bwb.room.BabbleDatabase
import com.learning.leap.bwb.utility.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AgeRangeViewModel @Inject constructor(
    private val babbleDatabase: BabbleDatabase,
    private val dataStore: DataStore<Preferences>,
    private val mapper: DynamoDBMapper,
) : ViewModel() {
    private val _currentIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex
    private val _goToDowloadPage: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val goToDownloadPage: StateFlow<Boolean> = _goToDowloadPage
    private val _showSaveButton: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showSaveButton: StateFlow<Boolean> = _showSaveButton
    var incomingIndex = 0


    init {
        getCurrentBucketIndex()
    }

    private fun getCurrentBucketIndex() {
        viewModelScope.launch {
            val bucket = dataStore.data.map { pref ->
                pref[intPreferencesKey(Constant.AGE_RANGE_BUCKET)]
            }.first() ?: 0
            _currentIndex.value = bucket
            incomingIndex = bucket
        }
    }

    fun updateBucketIndex(index: Int) {
        _currentIndex.value = index
        _showSaveButton.value = index != incomingIndex
    }

    fun getNewTips() {
        viewModelScope.launch {
            dataStore.edit { pref ->
                pref[intPreferencesKey(Constant.AGE_RANGE_BUCKET)] = currentIndex.value
            }
            withContext(Dispatchers.IO) {
                val tips = retrieveNotifications(getAgeRangeBucket(currentIndex.value)).toList()
                babbleDatabase.babbleTipDAO().deleteAllTips()
                babbleDatabase.babbleTipDAO().insertAll(tips)
            }
            withContext(Dispatchers.Main) {

                _goToDowloadPage.value = true
            }
        }
    }

    private fun retrieveNotifications(
        babyAge: Int,
    ): PaginatedScanList<BabbleTip> {
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


    private fun getAgeRangeBucket(
        sharedPrefBirthDayInMonth: Int,
    ): Int {
        return when (sharedPrefBirthDayInMonth) {
            0 -> 2
            1 -> 5
            2 -> 8
            3 -> 11
            4 -> 14
            5 -> 20
            6 -> 28
            7 -> 32
            else -> 40
        }
    }

    fun setGoDownloadToFalse() {
        _goToDowloadPage.value = false
    }


}