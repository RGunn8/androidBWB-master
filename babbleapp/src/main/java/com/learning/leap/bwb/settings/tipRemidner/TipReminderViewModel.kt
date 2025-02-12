package com.learning.leap.bwb.settings.tipRemidner

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.learning.leap.bwb.localNotification.TipNotification
import com.learning.leap.bwb.localNotification.TipScheduler
import com.learning.leap.bwb.room.BabbleDatabase
import com.learning.leap.bwb.utility.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TipReminderViewModel @Inject constructor(
    private val mapper: DynamoDBMapper,
    private val dataStore: DataStore<Preferences>,
    private val babbleDatabase: BabbleDatabase
) : ViewModel() {

    private val _tipReminderState: MutableStateFlow<TipReminderState> =
        MutableStateFlow(TipReminderState())
    val tipReminderState: StateFlow<TipReminderState> = _tipReminderState
    var tipOne: TipNotification = TipNotification(0, 1, 1)
    var tipTwo: TipNotification = TipNotification(0, 1, 1)

    init {
        loadTipTimes()
    }

    private fun loadTipTimes() {
        viewModelScope.launch {
            dataStore.data.collectLatest {
                val firstHour = it[intPreferencesKey(Constant.TIP_ONE_HOURS)] ?: 7
                val secondHour = it[intPreferencesKey(Constant.TIP_TWO_HOURS)] ?: 19
                val firstMin = it[intPreferencesKey(Constant.TIP_ONE_MINS)] ?: 0
                val secondMin = it[intPreferencesKey(Constant.TIP_TWO_MINS)] ?: 0
                val tipOneOn = it[booleanPreferencesKey(Constant.TIP_ONE_ON)] ?: false
                val tipTwoOn = it[booleanPreferencesKey(Constant.TIP_TWO_ON)] ?: false
                val numberOfTips = it[intPreferencesKey(Constant.NUM_OF_TIPS)] ?: 2
                tipOne = TipNotification(1000, numberOfTips, 1)
                tipTwo = TipNotification(1000, numberOfTips, 2)
                _tipReminderState.value = _tipReminderState.value.copy(
                    firstReminderHour = firstHour,
                    firstReminderMin = firstMin,
                    secondReminderHour = secondHour,
                    secondReminderMin = secondMin,
                    firstReminderSwitchEnabled = tipOneOn,
                    secondReminderSwitchEnabled = tipTwoOn,
                    numOfTips = numberOfTips,
                )

            }
        }
    }


    fun updateReminderTime(isFirstTip: Boolean, hour: Int, min: Int) {
        if (isFirstTip) {
            _tipReminderState.value = _tipReminderState.value.copy(
                firstReminderHour = hour,
                firstReminderMin = min
            )
        } else {
            _tipReminderState.value = _tipReminderState.value.copy(
                secondReminderHour = hour,
                secondReminderMin = min
            )
        }
    }

    fun updateSwitch(isFirstTip: Boolean, enabled: Boolean) {
        if (isFirstTip) {
            _tipReminderState.value = _tipReminderState.value.copy(
                firstReminderSwitchEnabled = enabled
            )
        } else {
            _tipReminderState.value = _tipReminderState.value.copy(
                secondReminderSwitchEnabled = enabled
            )
        }
    }

    fun saveChanges(tipScheduler: TipScheduler) {
        tipScheduler.cancel(tipOne)
        tipScheduler.cancel(tipTwo)
        viewModelScope.launch {
            dataStore.edit {
                it[intPreferencesKey(Constant.TIP_ONE_HOURS)] =
                    tipReminderState.value.firstReminderHour
                it[intPreferencesKey(Constant.TIP_TWO_HOURS)] =
                    tipReminderState.value.secondReminderHour
                it[intPreferencesKey(Constant.TIP_ONE_MINS)] =
                    tipReminderState.value.firstReminderMin
                it[intPreferencesKey(Constant.TIP_TWO_MINS)] =
                    tipReminderState.value.secondReminderMin
                it[booleanPreferencesKey(Constant.TIP_ONE_ON)] =
                    tipReminderState.value.firstReminderSwitchEnabled
                it[booleanPreferencesKey(Constant.TIP_TWO_ON)] =
                    tipReminderState.value.secondReminderSwitchEnabled
                it[intPreferencesKey(Constant.NUM_OF_TIPS)] = tipReminderState.value.numOfTips
            }
        }

        if (tipReminderState.value.firstReminderSwitchEnabled) {
            scheduleTip(
                tipScheduler,
                tipReminderState.value.firstReminderHour,
                tipReminderState.value.firstReminderMin,
                1
            )
        } else {
            cancelTip(
                tipScheduler,
                tipReminderState.value.firstReminderHour,
                tipReminderState.value.firstReminderMin,
                1
            )
        }

        if (tipReminderState.value.secondReminderSwitchEnabled) {
            scheduleTip(
                tipScheduler,
                tipReminderState.value.secondReminderHour,
                tipReminderState.value.secondReminderMin,
                2
            )
        } else {
            cancelTip(
                tipScheduler,
                tipReminderState.value.secondReminderHour,
                tipReminderState.value.secondReminderMin,
                2
            )
        }
    }

    private fun scheduleTip(tipScheduler: TipScheduler, hour: Int, min: Int, id: Int) {
        val calendar = Calendar.getInstance()
        val time = LocalTime.of(hour, min)
        val date = time.atDate(LocalDate.now().plusDays(1))
        val tipNotification = TipNotification(
            date.toEpochSecond(ZoneOffset.UTC),
            tipReminderState.value.numOfTips,
            id
        )
        tipScheduler.schedule(tipNotification)
    }

    private fun cancelTip(tipScheduler: TipScheduler, hour: Int, min: Int, id: Int) {
        val tipNotification = TipNotification(
            Instant.now().epochSecond,
            tipReminderState.value.numOfTips,
            id
        )
        tipScheduler.cancel(tipNotification)
    }

    fun updateNumberOfTips(numberOfTips: Int) {
        _tipReminderState.value = _tipReminderState.value.copy(
            numOfTips = numberOfTips,
        )
    }
}
