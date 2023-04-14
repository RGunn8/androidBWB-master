package com.learning.leap.bwb.vote;

import com.learning.leap.bwb.baseInterface.BaseNotificationPresenter;
import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.room.BabbleDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class VotePresenter extends BaseNotificationPresenter {
    private int numberOfTips;
    private int bucketNumber;
    private VoteViewViewInterface voteViewInterface;

    VotePresenter(int numberOfTips, int bucketNumber, VoteViewViewInterface voteViewInterface) {
        this.numberOfTips = numberOfTips;
        this.bucketNumber = bucketNumber;
        this.voteViewInterface = voteViewInterface;
    }

    @Override
    public Single<List<BabbleTip>> getRealmResults() {
        return null;
    }

    @Override
    public void onCreate() {
        setBaseNotificationViewInterface(voteViewInterface);
        babyName = baseNotificationViewInterface.babyName();
        Disposable disposable = getRealmResults().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Consumer<List<BabbleTip>>) babbleTips -> {
                    setNotifications(babbleTips);
                    if (notifications.size() == 0) {
                        voteViewInterface.homeIntent();
                    } else {
                        displayPrompt();
                    }
                }, Throwable::printStackTrace);
        disposables.add(disposable);
    }

    private Boolean doHomeIntent() {
        return index == numberOfTips - 1;
    }

    void thumbUpButtonTapped() {
        updateRandomNotification();
        checkForHomeIntent();
    }

    void thumbDownButtonTapped() {
        updateRandomNotification();
        checkForHomeIntent();
    }

    private void checkForHomeIntent() {
        if (doHomeIntent()) {
            voteViewInterface.homeIntent();
        } else {
            index++;
            updateView();

        }
    }

    private void updateRandomNotification() {
        BabbleTip tip = tipAtIndex();
        tip.setPlayToday(true);
        Completable.fromAction(() -> {
            BabbleDatabase.Companion.getInstance(null).babbleTipDAO().updateTip(tip);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

}
