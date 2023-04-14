package com.learning.leap.bwb.userInfo

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.learning.leap.bwb.R
import com.learning.leap.bwb.download.DownloadActivity
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.model.BabbleUser
import com.learning.leap.bwb.settings.WhyActivity
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.DynamoDBSingleton
import com.learning.leap.bwb.utility.NetworkChecker
import com.learning.leap.bwb.utility.Utility
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*


class UserInfoActivity : AppCompatActivity(), UserInfoViewInterface,
    DatePickerDialog.OnDateSetListener {
    var newUser: Boolean = false
    var gender: String = "Now Now"
    var date = ""
    var dialogIsShowing = false
    var mDialog: ProgressDialog? = null

    lateinit var userInfoPresenter: UserInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        onClickListners()
        setUpDateTextView()
        setActionBarBackgroundColor()
        newUser = intent.getBooleanExtra(Constant.NEW_USER, false)
        userInfoPresenter = UserInfoPresenter(
            newUser, this, LocalLoadSaveHelper(this),
            NetworkChecker(this),
            DynamoDBSingleton.getDynamoDB(this)
        )
        if (!newUser) {
            userInfoPresenter.loadPlayerFromSharedPref()
        }
    }

    private fun setActionBarBackgroundColor() {
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.dark_green
                )
            )
        )
        supportActionBar?.title = "User Information"
        if (!newUser) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            finish()
        }
        return true

    }

    private fun setUpDateTextView() {
//        userProfileBirtdayEditText.isFocusable = false
//        userProfileBirtdayEditText.isClickable = true
        val now = Calendar.getInstance()
        val dpd: DatePickerDialog = DatePickerDialog.newInstance(
            this,
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH] // Inital day selection
        )
        dpd.accentColor = ContextCompat.getColor(this, R.color.darkestBlue)
        dpd.setOkColor(Color.BLACK)
        dpd.setCancelColor(Color.BLACK)
        dpd.setOnCancelListener {
            dialogIsShowing = false
        }
//        userProfileBirtdayEditText.setOnClickListener {
//            if (!dialogIsShowing) {
//                dialogIsShowing = true
//                dpd.show(this.supportFragmentManager, null)
//            }
//
//        }
    }

    private fun onClickListners() {
//        pleaseTapHereTextView.setOnClickListener { view: View? -> whyActivityIntent() }
//        userProfileSaveButton.setOnClickListener { view: View? -> saveButtonClicked() }
//        userMaleButton.setOnClickListener {
//            maleButtonSelected()
//            gender = "Male"
//        }
//
//        userFemaleButton.setOnClickListener {
//            femaleButonSelected()
//            gender = "Female"
//        }
//
//        userNotNowButton.setOnClickListener {
//            notNowSelected()
//            gender = "Not Now"
//        }
    }

    private fun notNowSelected() {
//        userMaleButton.backgroundTintList = greyBackgroundTint()
//        userFemaleButton.backgroundTintList = greyBackgroundTint()
//        userNotNowButton.backgroundTintList = darkBlueeBackgroundTint()
    }

    private fun femaleButonSelected() {
//        userMaleButton.backgroundTintList = greyBackgroundTint()
//        userFemaleButton.backgroundTintList = darkBlueeBackgroundTint()
//        userNotNowButton.backgroundTintList = greyBackgroundTint()
    }

    private fun maleButtonSelected() {
//        userMaleButton.backgroundTintList = darkBlueeBackgroundTint()
//        userFemaleButton.backgroundTintList = greyBackgroundTint()
//        userNotNowButton.backgroundTintList = greyBackgroundTint()
    }

    private fun greyBackgroundTint(): ColorStateList? {
        return ContextCompat.getColorStateList(this, R.color.light_grey)
    }

    private fun darkBlueeBackgroundTint(): ColorStateList? {
        return ContextCompat.getColorStateList(this, R.color.darkestBlue)
    }

    override fun onDestroy() {
        super.onDestroy()
        userInfoPresenter.onDestroy()
    }

    private fun whyActivityIntent() {
        val whyIntent = Intent(this, WhyActivity::class.java)
        startActivity(whyIntent)
    }

    private fun saveButtonClicked() {
        userInfoPresenter.createBabblePlayer(createBabblePlayer())
        userInfoPresenter.checkUserInput()
    }

    private fun createBabblePlayer(): BabbleUser {
        return BabbleUser(
            UUID.randomUUID().toString(),
            date,
           // userProfileFragmentFirstNameEditText.text.toString().trim { it <= ' ' },
            gender,
            //optionalCode.text.toString()
        )

    }

    override fun displayErrorDialog(dialogTitle: Int, dialogMessage: Int) {
        runOnUiThread { Utility.displayAlertMessage(dialogTitle, dialogMessage, this) }
    }

    override fun displayUserInfo(babbleUser: BabbleUser) {
//        userProfileBirtdayEditText.setText(babbleUser.babyBirthday)
//        userProfileFragmentFirstNameEditText.setText(babbleUser.babyName)
        when (babbleUser.babyGender) {
            "Male" -> maleButtonSelected()
            "Female" -> femaleButonSelected()
            else -> notNowSelected()
        }
    }

    override fun dismissActivity() {
        Utility.addCustomEvent(
            Constant.CHANGE_PROFILE_SETTINGS,
            Utility.getUserID(this),
            null,
            this
        )
        finish()
    }

    override fun downloadIntent() {
        val downloadIntent = Intent(this@UserInfoActivity, DownloadActivity::class.java)
        startActivity(downloadIntent)
    }


    override fun getUserGender(): String {
        return gender
    }

    override fun displaySaveDialog() {
        runOnUiThread { createDialog() }
    }

    private fun createDialog() {
        mDialog = ProgressDialog(this@UserInfoActivity)
        mDialog!!.setMessage("Saving Player Info")
        mDialog!!.setTitle("Beginning to Babble")
        mDialog!!.show()
    }

    override fun dismissSaveDialog() {
        if (mDialog != null) {
            runOnUiThread { mDialog!!.dismiss() }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val correctMonth = monthOfYear + 1
        date = "$correctMonth/$dayOfMonth/$year"
        //userProfileBirtdayEditText.setText(date)
        dialogIsShowing = false
    }
}