package com.learning.leap.bwb.userInfo

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.learning.leap.bwb.R
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.model.BabbleTip
import com.learning.leap.bwb.model.BabbleUser
import com.learning.leap.bwb.room.BabbleDatabase
import com.learning.leap.bwb.utility.NetworkCheckerInterface
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class UserInfoPresenter(
    private val newUser: Boolean,
    private val userInfoViewInterface: UserInfoViewInterface,
    private val saveHelper: LocalLoadSaveHelper,
    private val networkCheckerInterface: NetworkCheckerInterface,
    private val mapper: DynamoDBMapper
) {
    private var babblePlayer: BabbleUser? = null
    private val disposables = CompositeDisposable()

    fun onDestroy() {
        disposables.clear()
    }

    private fun updatePlayer() {
        babblePlayer?.let {
            val disposable = Completable.fromAction {
                if (networkCheckerInterface.isConnected) {
                    it.saveBabbleUser(mapper, saveHelper)
                    BabbleDatabase.getInstance().babbleUserDAO().deleteUser()
                    BabbleDatabase.getInstance().babbleUserDAO().addUser(it)
                } else {
                    throw java.lang.Exception()
                }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    updatePlayerOnCompleted()
                }, {
                    it.printStackTrace()
                    userInfoViewInterface.displayErrorDialog(
                        R.string.BabbleError,
                        R.string.userSave
                    )
                })
            disposables.add(disposable)
        }
    }

    private fun updatePlayerOnCompleted() {
        if (networkCheckerInterface.isConnected) {
            if (newUser) {
                retrieveNotificationsFromAmazon()
            } else {
                userInfoViewInterface.dismissActivity()
            }
        } else {
            userInfoViewInterface.displayErrorDialog(R.string.BabbleError, R.string.noConnection)
        }
    }

    private fun retrieveNotificationsFromAmazon() {
        babblePlayer?.let {
            Single.fromCallable {
                return@fromCallable it.retrieveNotifications(it.userAgeInMonth, mapper)
            }.doOnSubscribe { userInfoViewInterface.displaySaveDialog() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ tips ->
                    updateViewAfterRetrievingNotificationList(tips)
                }, {
                    it.printStackTrace()
                    updateViewAfterError()
                })
        }
    }

    private fun updateViewAfterError() {
        userInfoViewInterface.dismissSaveDialog()
        userInfoViewInterface.displayErrorDialog(R.string.BabbleError, R.string.downloadError)
    }

    private fun updateViewAfterRetrievingNotificationList(notifications: List<BabbleTip>) {
        userInfoViewInterface.dismissSaveDialog()
        if (notifications.isEmpty()) {
            userInfoViewInterface.displayErrorDialog(
                R.string.BabbleError,
                R.string.noPromptsForUser
            )
        } else {
            saveNotifications(notifications)
        }
    }

    private fun saveNotifications(notifications: List<BabbleTip>) {
        saveHelper.saveNotificationSize(notifications.size)
        val result = Completable.fromAction {
            BabbleDatabase.getInstance().babbleTipDAO().deleteAllNotifications()
            BabbleDatabase.getInstance().babbleTipDAO().insertAll(notifications)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                updateBabblePlayer()
            }
        disposables.add(result)
    }

    private fun updateBabblePlayer() {
        babblePlayer?.let {
            Completable.fromAction { BabbleDatabase.getInstance().babbleUserDAO().addUser(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    userInfoViewInterface.downloadIntent()
                }
        }
    }


    fun createBabblePlayer(user: BabbleUser) {
        babblePlayer = user
        babblePlayer?.birthdayDate = BabbleUser.checkDate(user.babyBirthday)
        babblePlayer?.userAgeInMonth = BabbleUser.setUserAgeInMonth(user.babyBirthday)
    }

    fun loadPlayerFromSharedPref() {
        babblePlayer = BabbleUser.loadBabblePlayerFromSharedPref(saveHelper)
        userInfoViewInterface.displayUserInfo(babblePlayer!!)
    }

    fun checkUserInput() {
        babblePlayer?.let {
            if (it.checkIfPlayerIsValid()) {
                updatePlayer()
            } else if (it.checkNameIsEmpty()) {
                userInfoViewInterface.displayErrorDialog(
                    R.string.userNameNameErrorTitle,
                    R.string.userNameEmptyError
                )
            } else if (it.checkNameIsTooLong()) {
                userInfoViewInterface.displayErrorDialog(
                    R.string.userNameNameErrorTitle,
                    R.string.userNameToLongError
                )
            } else if (BabbleUser.checkDate(it.babyBirthday) == null) {
                userInfoViewInterface.displayErrorDialog(
                    R.string.userBirthdayErrorTitle,
                    R.string.userBirthdayError
                )
            } else if (it.userAgeInMonth <= 0) {
                userInfoViewInterface.displayErrorDialog(
                    R.string.BabbleError,
                    R.string.userNotBornYetError
                )
            }
        }

    }

}