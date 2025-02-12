package com.learning.leap.bwb.settings.tipRemidner

data class TipReminderState(
    val firstReminderHour: Int = 6,
    val firstReminderMin: Int = 0,
    val firstReminderSwitchEnabled: Boolean = false,
    val secondReminderHour: Int = 17,
    val secondReminderMin: Int = 0,
    val secondReminderSwitchEnabled: Boolean = false,
    val numOfTips: Int = 2,
)