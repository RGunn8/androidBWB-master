package com.learning.leap.bwb.settings

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.learning.leap.bwb.R
import com.learning.leap.bwb.baseActivity.DetailActivity
import com.learning.leap.bwb.baseActivity.HomeActivity
import com.learning.leap.bwb.tipSettings.TipSettingsPresenters
import com.learning.leap.bwb.tipSettings.TipSettingsViewInterface
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.Utility
import kotlinx.android.synthetic.main.activity_tip_settings.*

class TipSettingsActivity : AppCompatActivity(), TipSettingsViewInterface {

    lateinit var tipSettingsPresenters: TipSettingsPresenters
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_settings)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.dark_green
                )
            )
        )
        supportActionBar?.title = "Tip Reminder Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setOnClickListners()
        tipSettingsPresenters = TipSettingsPresenters(this, this)
        tipSettingsPresenters.onCreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            finish()
        }
        return true

    }


    private fun setOnClickListners() {
        tipSettingSaveButton.setOnClickListener { tipSettingsPresenters.save() }
        tipsettingsMaxNumberTipMinusButton.setOnClickListener { tipSettingsPresenters.userMaxTipMinusButtonPressed() }
        tipSettingsMaxTipNumberPlusButton.setOnClickListener { tipSettingsPresenters.userMaxTipsPlusButtonPressed() }
        tipSettingsStartTimeMinusButton.setOnClickListener { tipSettingsPresenters.startTimeMinusButtonPressed() }
        tipSettingsStartTimePlusButton.setOnClickListener { tipSettingsPresenters.startTimePlusButtonPressed() }
        tipSettingsEndTimeMinusButton.setOnClickListener { tipSettingsPresenters.endTimeMinusButtonPressed() }
        tipSettingsEndTimePlusButton.setOnClickListener { tipSettingsPresenters.endTimePlusButtonPressed() }
        firstTipSwitch.setOnCheckedChangeListener { _, _ ->
            tipSettingsPresenters.turnOfSwitchChangedListener()
        }
        secondTipSwitch.setOnCheckedChangeListener { _, _ ->
            tipSettingsPresenters.turnOfSwitchChangedListener()
        }

    }

    override fun displayFirstSwitch(isOn: Boolean) {
        firstTipSwitch.isChecked = isOn
    }

    override fun displaySecondSwitch(isOn: Boolean) {
        secondTipSwitch.isChecked = isOn
    }

    override fun displayStartTimeAtIndex(startTime: String) {
        tipSettingsStartTimeTextView.text = startTime
    }

    override fun displayEndTimeAtIndex(endTime: String) {
        tipSettingsEndTimeTextView.text = endTime
    }

    override fun displayUserMaxTipsAtIndex(maxTips: String) {
        tipSettingsMaxNumberTipTextView.text = maxTips
    }

    override fun hideStartTimePlusSigned() {
        tipSettingsStartTimePlusButton.isEnabled = false
    }

    override fun hideStartTimeMinusSigned() {
        tipSettingsStartTimeMinusButton.isEnabled = false
    }

    override fun hideEndTimePlusSigned() {
        tipSettingsEndTimePlusButton.isEnabled = false
    }

    override fun hideEndTimeMinusSigned() {
        tipSettingsEndTimeMinusButton.isEnabled = false
    }

    override fun hideMaxNumberOfTipsPlusSigned() {
        tipSettingsMaxTipNumberPlusButton.isEnabled = false
    }

    override fun hideMaxNumberOfTipsMinusSigned() {
        tipsettingsMaxNumberTipMinusButton.isEnabled = false
    }

    override fun setTipSwitch(sendTipsToday: Boolean) {
        //sendTipsTodaySwitch!!.isChecked = sendTipsToday
    }

    override fun displayStartTimePlusSigned() {
        tipSettingsStartTimePlusButton.isEnabled = true
    }

    override fun displayStartTimeMinusSigned() {
        tipSettingsStartTimeMinusButton.isEnabled = true
    }

    override fun displayEndTimePlusSigned() {
        tipSettingsEndTimePlusButton.isEnabled = true
    }

    override fun displayEndTimeMinusSigned() {
        tipSettingsEndTimeMinusButton.isEnabled = true
    }

    override fun displayMaxNumberOfTipsPlusSigned() {
        tipSettingsMaxTipNumberPlusButton.isEnabled = true
    }

    override fun displayMaxNumberOfTipsMinusSigned() {
        tipsettingsMaxNumberTipMinusButton.isEnabled = true
    }

    override fun displaySaveButton() {
        tipSettingSaveButton.visibility = View.VISIBLE
    }

    override fun displayErrorMessage() {
        Utility.displayAlertMessage(R.string.BabbleError, R.string.times_are_incorrect, this)
    }

    override fun tipOneSwitchIsOn(): Boolean {
        return firstTipSwitch.isChecked
    }

    override fun tipTwoSwitchIsOn(): Boolean {
        return secondTipSwitch.isChecked
    }

    override fun saveCompleted(turnOffTips: Boolean) {
        if (turnOffTips) {
            Utility.addCustomEvent(
                Constant.TURNED_OFF_NOTIFICATIONS_FOR_DAY,
                Utility.getUserID(this),
                null,
                this
            )
        }
        Utility.addCustomEvent(
            Constant.CHANGED_NOTIFICATION_SETTINGS,
            Utility.getUserID(this),
            null,
            this
        )
        finish()
    }

    override fun onHomePress() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeIntent)
    }

    override fun onSettingsPress() {}
    override fun onPlayToday() {
        val playTodayIntent = Intent(this, DetailActivity::class.java)
        playTodayIntent.putExtra(DetailActivity.DETAIL_INTENT, DetailActivity.PLAY_TODAY)
        startActivity(playTodayIntent)
    }

    override fun onLibraryPress() {
        val libraryIntent = Intent(this, DetailActivity::class.java)
        libraryIntent.putExtra(DetailActivity.DETAIL_INTENT, DetailActivity.LIBRARY)
        startActivity(libraryIntent)
    }
}